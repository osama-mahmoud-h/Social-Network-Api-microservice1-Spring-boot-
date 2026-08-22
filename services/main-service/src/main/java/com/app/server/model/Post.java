package com.app.server.model;

import com.app.server.enums.PostPublicity;
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
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "post_sequence", sequenceName = "post_sequence", allocationSize = 50)  // Adjust allocationSize as needed
    @Column(name = "post_id")
    private Long postId;

    @Column(nullable = false, length = 512)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'PUBLIC'")
    private PostPublicity publicity = PostPublicity.PUBLIC;

    @Column(nullable = false, updatable = false)
    @JsonDeserialize(as = InstantDeserializer.class)
    private Instant createdAt;

    @JsonDeserialize(as = InstantDeserializer.class)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "author_id",
            referencedColumnName = "user_id",
            nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_posts_author_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserProfile author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_files",
            joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "post_id", foreignKey = @ForeignKey(name = "FK_post_files_post_id")),
            inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "file_id", foreignKey = @ForeignKey(name = "FK_post_files_file_id"))
    )
    private Set<File> files;

    public static Post publish(UserProfile author, String content, PostPublicity publicity, Set<File> files) {
        Post post = new Post();
        post.author = author;
        post.content = content;
        post.publicity = publicity != null ? publicity : PostPublicity.PUBLIC;
        post.files = files != null ? files : new HashSet<>();
        post.createdAt = Instant.now();
        return post;
    }

    public void edit(Long actorId, String newContent, PostPublicity newPublicity) {
        if (!this.author.getUserId().equals(actorId)) {
            throw new com.app.server.exception.CustomRuntimeException("Unauthorized to update this post", org.springframework.http.HttpStatus.FORBIDDEN);
        }
        if (newContent != null && !newContent.trim().isEmpty()) {
            this.content = newContent;
        }
        if (newPublicity != null) {
            this.publicity = newPublicity;
        }
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return postId != null && postId.equals(post.getPostId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
