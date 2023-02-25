package com.example.server.models;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Entity(name = "following")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//public class Following {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @ManyToOne
//    @JsonIgnoreProperties(value = {"followers","following"})
//    private User follower;
//
//    @ManyToOne
//    @JsonIgnoreProperties(value = {"followers","following"})
//    private User followed;
//
//    // constructors, getters, and setters
//}
