package com.photo.photoblog.dao.repository;

import com.photo.photoblog.dao.entities.Comment;
import com.photo.photoblog.dao.entities.Photo;
import com.photo.photoblog.dao.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPhoto(Photo photo);

    List<Comment> findAllByPhotoId(Long photoId);

    List<Comment> findAllByUsers(Users users);
}
