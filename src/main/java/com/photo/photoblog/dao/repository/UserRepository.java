package com.photo.photoblog.dao.repository;

import com.photo.photoblog.dao.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("select u from Users u where u.username = ?1")
    Users findByUsername(String username);

    Users findByEmail(String currentUserName);
}

