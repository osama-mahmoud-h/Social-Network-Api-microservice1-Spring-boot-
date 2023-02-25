package com.example.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "profiles")
@NoArgsConstructor
@Setter
@Getter
@ToString
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Type(type = "jsonb")
    @Column(length = 500,columnDefinition = "jsonb")
    @JsonIgnoreProperties(value = "profile")
    private User owner;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "profile_id")
//    Set<Skill>skills = new HashSet<Skill>();

    @Column(nullable = true)
    private String education;

    @Column(nullable = true)
    private String aboutUser;

    @Column(nullable = true)
    private String bio;

    @Column(nullable = true)
    private String image_url;


}
