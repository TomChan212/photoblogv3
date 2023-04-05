package com.photo.photoblog.dao.repository;

import com.photo.photoblog.dao.entities.Comment;
import com.photo.photoblog.dao.entities.Photo;
import com.photo.photoblog.dao.entities.CommentHistory;
import com.photo.photoblog.dao.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentHistoryRepository extends JpaRepository<CommentHistory, Long> {

    List<CommentHistory> findAllByUsers(Users users);
}
