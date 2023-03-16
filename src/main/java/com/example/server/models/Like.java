package com.example.server.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "users_like_posts")
@Setter
@Getter
@ToString(exclude = {"post","liker"})
//@AllArgsConstructor
//@NoArgsConstructor
@EqualsAndHashCode(exclude = {"post", "liker"})
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    @JsonIgnoreProperties(value = {"author","likedPosts"})
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liker_id")
    @JsonIgnore
    private User liker;

    @Column(name = "type")
    private byte type;

    public Like() {
        this.type = 0 ;
    }

    public Like(Post post, User liker, byte type) {
        this.post=post;
        this.liker = liker;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return type == like.type && Objects.equals(id, like.id) && Objects.equals(post, like.post) && Objects.equals(liker, like.liker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, post, liker, type);
    }
}
