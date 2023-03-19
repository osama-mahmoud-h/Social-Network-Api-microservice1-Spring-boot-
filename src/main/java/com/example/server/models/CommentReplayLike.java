package com.example.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@EqualsAndHashCode(exclude = {"commentReplay","liker"})
@Entity
@Table(name = "users_like_comments_replies")
public class CommentReplayLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "replay_id")
    @JsonIgnoreProperties(value = {"author","likedReplies"})
    @JsonIgnore
    private CommentReplay commentReplay;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liker_id")
    @JsonIgnore
    private User liker;

    @Column(name = "type")
    private byte type;

}
