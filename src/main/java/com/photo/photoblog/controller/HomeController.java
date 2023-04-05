package com.photo.photoblog.controller;

import com.photo.photoblog.dao.entities.Photo;
import com.photo.photoblog.dao.entities.Users;
import com.photo.photoblog.dao.repository.PhotoRepository;
import com.photo.photoblog.dao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller

public class HomeController implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model) {
        List<Photo> photos = photoRepository.findAll();
        model.addAttribute("photos", photos);
        return "index";
    }

    @GetMapping("/register")
    public ModelAndView register(Model model){
        //userService.register(model);
        model.addAttribute("users", new Users());
        ModelAndView mav = new ModelAndView("/register");
        return mav;
    }
    @PostMapping("/registerSuccess")
    public ModelAndView register_success(Users users, Model model, Principal principal) {
            //userService.registerSuccess(users);
            users.setRole("ROLE_USER");
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            userRepository.save(users);
            if(principal != null){
                ModelAndView mav = new ModelAndView("redirect:/admin/adminIndex");
                return mav;
            }
            else{
                ModelAndView mav = new ModelAndView("redirect:/login");
                return mav;
            }

    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @Override
    public void run(String... args) throws Exception {
        runme();
    }


    public void runme(){
        if(userRepository.findByUsername("admin") == null){
        Users users = new Users();
        users.setUsername("admin");
        users.setRole("ROLE_ADMIN");
        users.setEmail("admin@gmail.com");
        users.setPhoneNumber("999276533");
        users.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(users);
        }
        else {
            System.out.println("admin already exists");
        }

    }


}