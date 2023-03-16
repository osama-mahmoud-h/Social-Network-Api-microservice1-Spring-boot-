package com.example.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@EqualsAndHashCode(exclude = {"comment","liker"})
@Entity
@Table(name="users_like_comments")
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id")
    @JsonIgnoreProperties(value = {"author","likedComments"})
    @JsonIgnore
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liker_id")
    @JsonIgnore
    private User liker;

    @Column(name = "type")
    private byte type;

    public CommentLike() {
        this.type = 0 ;
    }



}
