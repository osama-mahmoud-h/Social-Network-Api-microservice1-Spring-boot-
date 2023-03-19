package com.example.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.sql.Timestamp;
import java.util.*;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name = "posts")
@Setter
@Getter
@ToString(exclude = {"author", "comments","likedPosts"})
@NoArgsConstructor
@TypeDefs({
        @TypeDef(
                name = "string-array",
                typeClass = StringArrayType.class
        )
})
@EqualsAndHashCode(exclude ={"author", "comments","likedPosts"} )
public class Post {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @ManyToOne( fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @JsonIgnoreProperties(value = {"posts","liked_posts"})
    private User author;

    @OneToMany(mappedBy = "post",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties(value = {"post","liker"})
    @JsonIgnore
    private Set<PostLike>likedPosts = new HashSet<>();

    @Column(nullable = false,length = 2048)//2KB of string
    private String text;

    @Column(nullable = true)
    private String vedio_url;

    @Type(type = "string-array")
    @Column(
            length = 700,
            name = "images_url",
            columnDefinition = "text[]"
    )
    private String[] images_url ;

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




    public Post(String text, String vedio_url, String[] images_url, User user) {
        this.text = text;
        this.vedio_url = vedio_url;
        this.images_url = images_url;
     //   this.user = user;
        this.likes = 0L;
    }
   public Long getLikesCount() {
        return Long.valueOf(this.getLikedPosts().size());
    }


}
