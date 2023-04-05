package com.photo.photoblog.dao.entities;

import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@Table(name = "photohistory")
public class PhotoHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "image")
    private String image;


    @Column(name = "uploadDateTime",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDateTime;


    @ManyToOne
    private Users users;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public PhotoHistory() {
    }

    public PhotoHistory(String title, String description, String image, Users users) {
        this.title = title;
        this.description = description;
        this.users = users;
        this.uploadDateTime = new Date();
        this.comments = new ArrayList<>();
        this.image = image;

    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(Date uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", uploadDateTime=" + uploadDateTime +
                ", users=" + users +
                ", comments=" + comments +
                '}';
    }

    public void setDate(Date date) {
    }
}