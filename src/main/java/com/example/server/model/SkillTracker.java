package com.example.server.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "skills_tracker")
@Setter
@Getter
public class SkillTracker {
    @Id
    private Long id;

}
