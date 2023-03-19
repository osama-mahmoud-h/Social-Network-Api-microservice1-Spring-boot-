package com.example.server.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode(exclude = {"author", "comment"})
@Entity
@Table(name = "comments_replies")
public class CommentReplay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id")
    @JsonIgnoreProperties(value = {"author","likedComments"})
    @JsonIgnore
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    @JsonIgnore
    private User author;

    @OneToMany(mappedBy = "commentReplay",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties(value = {"commentReplay","liker"})
    @JsonIgnore
    private Set<CommentReplayLike> likedCommentReplies = new HashSet<>();

    @Column(name = "text",length = 225)
    private String text;

    @Column(nullable = true,name = "timestamp")
    @CreationTimestamp
    private Timestamp timestamp;

}
