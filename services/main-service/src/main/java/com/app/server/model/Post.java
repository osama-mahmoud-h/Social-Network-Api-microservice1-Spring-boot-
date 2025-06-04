package com.app.server.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "posts")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "post_sequence", sequenceName = "post_sequence", allocationSize = 50)  // Adjust allocationSize as needed
    private Long postId;

    @Column(nullable = false, length = 512)
    private String content;

    @Column(nullable = false, updatable = false)
    @JsonDeserialize(as = InstantDeserializer.class)
    private Instant createdAt;

    @JsonDeserialize(as = InstantDeserializer.class)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "author_id", referencedColumnName = "userId", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_posts_author_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_files",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "postId", foreignKey = @ForeignKey(name = "FK_post_files_post_id")),
            inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "fileId", foreignKey = @ForeignKey(name = "FK_post_files_file_id"))
    )
    private Set<File> files;

    //@OneToMany(fetch = FetchType.LAZY,  orphanRemoval = true, cascade = CascadeType.ALL)
    @Transient
    private Set<UserReaction> userReactions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @Transient
    private Integer commentsCount;

}
