package com.example.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import jakarta.persistence.*;
import java.util.Objects;

@Entity()
@Table(name = "users_like_posts")
@Setter
@Getter
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    @JsonIgnoreProperties(value = {"author","likedPosts"})
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liker_id")
    @JsonIgnore
    private AppUser liker;

    @Column(name = "type")
    private byte type;

    public PostLike() {
        this.type = 0 ;
    }

    public PostLike(Post post, AppUser liker, byte type) {
        this.post=post;
        this.liker = liker;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostLike like = (PostLike) o;
        return type == like.type && Objects.equals(id, like.id) && Objects.equals(post, like.post) && Objects.equals(liker, like.liker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, post, liker, type);
    }
}
