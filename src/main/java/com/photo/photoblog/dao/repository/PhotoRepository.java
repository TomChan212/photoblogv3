package com.photo.photoblog.dao.repository;

import com.photo.photoblog.dao.entities.Photo;
import com.photo.photoblog.dao.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PhotoRepository extends JpaRepository<Photo, Long> {

    public List<Photo> findAll();

    List<Photo> findAllByUsers(Users users);

}
