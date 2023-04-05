package com.photo.photoblog.controller;

import com.photo.photoblog.dao.entities.*;
import com.photo.photoblog.dao.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentHistoryRepository commentHistoryRepository;
    @Autowired
    private PhotoHistoryRepository photoHistoryRepository;

    //Show user profile
    @GetMapping("/profile")
    public ModelAndView showUserProfile(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        Users users = userRepository.findByEmail(currentUserName);
        model.addAttribute("users", users);
        ModelAndView mav = new ModelAndView("userProfile");
        return mav;
    }
    //show user index
    @GetMapping("/userIndex")
    public String home(Model model) {
        List<Photo> photos = photoRepository.findAll();
        model.addAttribute("photos", photos);
        return "userIndex";
    }
    //user profile update
    @GetMapping("/profile/update")
    public String showUpdateForm(Model model, Principal principal) {
        String currentUserEmail = principal.getName();
        Users users = userRepository.findByEmail(currentUserEmail);
        model.addAttribute("users", users);
        return "updateUserProfile";
    }


    @PostMapping("/profile/update")
    public String updateUserProfile(@ModelAttribute("users") Users users, Principal principal) {

        String currentUserEmail = principal.getName();
        Users currentUser = userRepository.findByEmail(currentUserEmail);
        users.setId(currentUser.getId());
        users.setUsername(currentUser.getUsername());
        users.setEmail(currentUser.getEmail());
        users.setRole(currentUser.getRole());
        users.setPassword(currentUser.getPassword());

        userRepository.save(users);
        return "redirect:/user/profile";
    }
//show my photos
    @GetMapping("/myPhotos")
    public String showUserPhotos(Model model, Principal principal) {
        String currentUserEmail = principal.getName();
        Users users = userRepository.findByEmail(currentUserEmail);
        model.addAttribute("photos", photoRepository.findAllByUsers(users));
        return "myPhotos";
    }
//show my comments history
    @GetMapping("/commentHistory")
    public String showCommentHistory(Model model, Principal principal) {
        String currentUserEmail = principal.getName();
        Users users = userRepository.findByEmail(currentUserEmail);
        List<CommentHistory> commentHistories = commentHistoryRepository.findAllByUsers(users);
        model.addAttribute("comment", commentHistories);
        return "commentHistoryShow";
    }
    //show my photo history
    @GetMapping("/photoHistory")
    public String showPhotoHistory(Model model, Principal principal) {
        String currentUserEmail = principal.getName();
        Users users = userRepository.findByEmail(currentUserEmail);
        List<PhotoHistory> photoHistories = photoHistoryRepository.findAllByUsers(users);
        model.addAttribute("photo", photoHistories);
        return "photoHistoryShow";
    }

    //upload photo
    @GetMapping("/photos/upload")
    public ModelAndView uploadPhoto(@ModelAttribute("photo") Photo photo, Model model){
        model.addAttribute("photo", new Photo());
        ModelAndView mav = new ModelAndView("/photoupload");
        return mav;
    }
    @PostMapping("/photosUploadSuccess")
    public ModelAndView uploadUserPhoto(@ModelAttribute Photo photo, @RequestParam("profileimage") MultipartFile file, Model model, Principal principal) throws IOException {
        String currentUser = principal.getName();
        Users users = userRepository.findByEmail(currentUser);
        photo.setUsers(users);
        photo.setUploadDateTime(new Date());

        //image upload handle
        if(file.isEmpty()){
            System.out.println("file is empty");
        }
        else{
            System.out.println("File name is" + file.getOriginalFilename());
            photo.setImage(file.getOriginalFilename());
            File uploadsDir = new File("src/main/resources/static");
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs();
            }
            File uploadsDir1 = new File("build/resources/static");
            if (!uploadsDir1.exists()) {
                uploadsDir1.mkdirs();
            }
            File saveFile = new ClassPathResource("static").getFile();

            String path_Directory = "src/main/resources/static";
            Files.copy(file.getInputStream(), Paths.get(path_Directory + File.separator + file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);

            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        }
        photoRepository.save(photo);

        PhotoHistory photoHistory = new PhotoHistory();
        photoHistory.setImage(photo.getImage());
        photoHistory.setUploadDateTime(photo.getUploadDateTime());
        photoHistory.setUsers(photo.getUsers());
        photoHistory.setTitle(photo.getTitle());
        photoHistory.setDescription(photo.getDescription());
        photoHistoryRepository.save(photoHistory);


        model.addAttribute("message", "Photo uploaded successfully");
        ModelAndView mav = new ModelAndView("/photouploadsuccess");
        return mav;
    }
    //delete photo
    @GetMapping("/photos/delete/{id}")
    public String deletePhoto(@PathVariable Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid photo Id:" + id));
        List<Comment> comments = commentRepository.findAllByPhoto(photo);
        commentRepository.deleteAll(comments);
        photoRepository.delete(photo);
        return "redirect:/user/userIndex";
    }
    //Update Photo
    @GetMapping("/photos/edit/{id}")
    public ModelAndView showUpdateForm(@PathVariable("id") Long id, Model model) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid photo Id:" + id));
        model.addAttribute("photo", photo);
        model.addAttribute("id", id);
        ModelAndView mav = new ModelAndView("updatePhoto");
        return mav;
    }
    @PostMapping("/photos/{id}/update")
    public ModelAndView updatePhoto(@PathVariable("id") String id, @ModelAttribute("photo") Photo photo, Model model) {
        System.out.println("photo id is" + id);
        Long photoId = Long.parseLong(id);
        Photo currentPhoto = photoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid photo Id:" + photoId));
        photo.setUploadDateTime(currentPhoto.getUploadDateTime());
        photo.setUsers(currentPhoto.getUsers());
        photoRepository.save(photo);
        ModelAndView mav = new ModelAndView("redirect:/user/photos/" + photoId);
        return mav;
    }
    //delete users
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        Users users = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        List<Photo> photos = photoRepository.findAllByUsers(users);
        List<Comment> comments = commentRepository.findAllByUsers(users);
        List<PhotoHistory> photoHistories = photoHistoryRepository.findAllByUsers(users);
        List<CommentHistory> commentHistories = commentHistoryRepository.findAllByUsers(users);
        commentHistoryRepository.deleteAll(commentHistories);
        photoHistoryRepository.deleteAll(photoHistories);
        commentRepository.deleteAll(comments);
        photoRepository.deleteAll(photos);
        userRepository.delete(users);
        return "redirect:/admin/adminIndex";
    }
//show photo page
    @GetMapping("/photos/{id}")
    public String showPhoto(@PathVariable Long id, Model model, Principal principal) {
        String currentUserEmail = principal.getName();
        Users users = userRepository.findByEmail(currentUserEmail);
        model.addAttribute("users", users);
        Photo photo = photoRepository.findById(id).orElse(null);
        model.addAttribute("photo", photo);
        Long photo_id = photo.getId();
        List<Comment> comment = commentRepository.findAllByPhotoId(photo_id);
        model.addAttribute("comments", comment);
        return "photo-page";
    }
    //show user in Admin panel
    @GetMapping("/userDetails/{id}")
    public String showUserDetails(@PathVariable Long id, Model model) {
        Users users = userRepository.findById(id).orElse(null);
        model.addAttribute("users", users);
        return "userDetails";
    }
//create new comment
    @PostMapping("/comment/{id}")
    public String addComment(@PathVariable Long id, @RequestParam("comment") String comment, Principal principal) {
        Photo photo = photoRepository.findById(id).orElse(null);
        String currentUserEmail = principal.getName();
        Users users = userRepository.findByEmail(currentUserEmail);
        Comment newComment = new Comment();
        newComment.setPhoto(photo);
        newComment.setUsers(users);
        newComment.setText(comment);
        newComment.setCreatedAt(new Date());
        newComment.setUpdatedAt(new Date());
        commentRepository.save(newComment);

        CommentHistory commentHistory = new CommentHistory();
        //commentHistory.setPhoto(photo);
        commentHistory.setUsers(users);
        commentHistory.setCreatedAt(new Date());
        commentHistory.setUpdatedAt(new Date());
        commentHistory.setText(comment);
        commentHistoryRepository.save(commentHistory);

        return "redirect:/user/photos/{id}";
    }
    //delete comment
    @GetMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id));
        Long pid = comment.getPhoto().getId();
        commentRepository.delete(comment);

        return "redirect:/user/photos/" + pid;
    }
    //delete comment history
    @GetMapping("/commentHistory/delete")
    public String deleteCommentHistory(Principal principal) {
        Users currentUser = userRepository.findByEmail(principal.getName());
        List<CommentHistory> commentHistories = commentHistoryRepository.findAllByUsers(currentUser);
        commentHistoryRepository.deleteAll(commentHistories);

        return "redirect:/user/commentHistory";
    }
    //delete photo history
    @GetMapping("/photoHistory/delete")
    public String deletePhotoHistory(Principal principal) {
        Users currentUser = userRepository.findByEmail(principal.getName());
        List<PhotoHistory> photoHistories = photoHistoryRepository.findAllByUsers(currentUser);
        photoHistoryRepository.deleteAll(photoHistories);

        return "redirect:/user/photoHistory";
    }
}