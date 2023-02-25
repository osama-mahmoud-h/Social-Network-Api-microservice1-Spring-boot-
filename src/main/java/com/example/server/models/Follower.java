package com.example.server.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "followers")
@Getter
@Setter
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties(value = {"followers","following"})
    private User follower;

    @ManyToOne
    @JsonIgnoreProperties(value = {"followers","following"})
    private User followed;

}
