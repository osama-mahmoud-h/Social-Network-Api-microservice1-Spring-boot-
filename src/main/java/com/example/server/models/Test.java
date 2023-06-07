package com.example.server.models;


import com.example.server.payload.request.profile.SocialRequestDto;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "test")
@Setter
@Getter
@NoArgsConstructor
@ToString
@TypeDefs({
        @TypeDef(
                name = "string-array",
                typeClass = StringArrayType.class
        ),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Type(type = "string-array")
    @Column(
            length = 512,
            name = "names",
            columnDefinition = "text[]"
    )
    private  SocialRequestDto[] names;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    Set<SocialRequestDto>tags = new HashSet<>();

}
