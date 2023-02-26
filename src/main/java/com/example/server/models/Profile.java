package com.example.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "profiles")
@NoArgsConstructor
@Setter
@Getter
@ToString
@TypeDefs({
        @TypeDef(
                name = "string-array",
                typeClass = StringArrayType.class
        ),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Type(type = "jsonb")
    @Column(length = 500,columnDefinition = "jsonb")
    @JsonIgnoreProperties(value = "profile")
    private User owner;

    @Type(type = "string-array")
    @Column(
            length = 250,
            name = "skills",
            columnDefinition = "text[]"
    )
    String[] skills ;

    @Column(nullable = true)
    private String education;

    @Column(nullable = true)
    private String aboutUser;

    @Column(nullable = true)
    private String bio;

    @Column(nullable = true)
    private String image_url;



}
