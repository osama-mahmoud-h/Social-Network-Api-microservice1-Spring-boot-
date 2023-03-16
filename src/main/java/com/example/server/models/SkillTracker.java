package com.example.server.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "skills_tracker")
@Setter
@Getter
public class SkillTracker {
    @Id
    private Long id;

}
