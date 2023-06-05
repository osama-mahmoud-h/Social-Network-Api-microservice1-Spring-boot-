package com.example.server.models;

import com.example.server.payload.request.profile.ContactInfoDto;
import com.example.server.payload.request.profile.EducationRequestDto;
import com.example.server.payload.request.profile.SocialRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.*;

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
            length = 512,
            name = "skills",
            columnDefinition = "text[]"
    )
    String[] skills ;

    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, String> links = new HashMap<>();

//    @ElementCollection
//    @CollectionTable(name = "your_entity_column_name")
//    @Column(name = "array_list_column")
//    private List<String> yourArrayList = new ArrayList<>();

    @Type(type = "jsonb")
    @Column(length = 256,columnDefinition = "jsonb")
    private ContactInfoDto contactInfo;

    @Type(type = "jsonb")
    @Column(length = 500,columnDefinition = "jsonb")
    private EducationRequestDto education;

    @Column(nullable = true)
    private String phone;

    @Column(nullable = true)
    private String aboutUser;

    @Column(nullable = true)
    private String bio;

    @Column(nullable = true)
    private String image_url;

    @Column(nullable = true)
    private String coverImageUrl;


}
