package com.photo.photoblog.dao.repository;

import com.photo.photoblog.dao.entities.PhotoHistory;
import com.photo.photoblog.dao.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoHistoryRepository extends JpaRepository<PhotoHistory, Long> {

    public List<PhotoHistory> findAll();

    List<PhotoHistory> findAllByUsers(Users users);



}
