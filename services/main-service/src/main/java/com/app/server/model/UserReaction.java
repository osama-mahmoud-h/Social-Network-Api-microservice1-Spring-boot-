package com.app.server.model;


import com.app.server.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "reactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "reaction_sequence", sequenceName = "reaction_sequence", allocationSize = 50)  // Adjust allocationSize as needed
    private Long reactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "author_id", referencedColumnName = "userId", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_reactions_author_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser author;

}
