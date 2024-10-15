package com.example.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "posts")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne( fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @JsonIgnoreProperties(value = {"posts","liked_posts"})
    private AppUser author;

    @OneToMany(mappedBy = "post",
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties(value = {"post","liker"})
    @JsonIgnore
    private Set<PostLike>likedPosts = new HashSet<>();

    @Column(nullable = false)
    private String text;

    @Column(nullable = true)
    private String vedio_url;

//    @Column(
//            length = 700,
//            name = "images_url",
//            columnDefinition = "text[]"
//    )
//    private String[] images_url ;

    @Column(nullable = true)
    private String file_url;

    @Transient
    private Long likes;

    @Column(nullable = true,name = "timestamp")
    @CreationTimestamp
    private Timestamp timestamp ;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL}
    )
    @JoinTable(name = "posts_comments",
            joinColumns = { @JoinColumn(name = "post_id") },
            inverseJoinColumns = { @JoinColumn(name = "comment_id") }
    )
    @OnDelete(action =OnDeleteAction.CASCADE)
    @JsonIgnoreProperties("author")
    private Set<Comment>comments;

}
