package com.app.server.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ForeignKey;
import lombok.*;
import org.hibernate.annotations.*;

import jakarta.persistence.*;

@Entity(name = "profiles")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long profileId;

    private String aboutUser;

    private String bio;

    private String imageUrl;

    @OneToOne(fetch = FetchType.LAZY, optional = false,cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_profiles_user_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

}
