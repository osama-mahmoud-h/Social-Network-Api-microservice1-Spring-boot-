package com.example.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity(name = "followers")
@Getter
@Setter
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = {"followers","following"})
    private AppUser follower;

    @ManyToOne
    @JsonIgnoreProperties(value = {"followers","following"})
    private AppUser followed;

}
