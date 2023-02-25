package com.example.server.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "skills")
@Data
public class Skill {
    @Id
    private Long id;

    @Column(unique = true,nullable = false)
    private String name;
}
