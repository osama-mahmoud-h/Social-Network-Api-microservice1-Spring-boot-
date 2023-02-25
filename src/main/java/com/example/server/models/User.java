package com.example.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@ToString(exclude = {"liked_posts","posts","profile"})  // create tostring function
@RequiredArgsConstructor // args constructors for dendency injection
//@NoArgsConstructor  //create empty constructor
//@AllArgsConstructor // create constructors takes all args
@Setter @Getter // setters and getters
@EqualsAndHashCode(exclude = {"liked_posts","posts","profile"})
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(unique = true,nullable = false)
  private String username;

  @Column(unique = true,nullable = false)
  private String email;

  @Column(nullable = false)
  @JsonIgnore
  private String password;

  @ManyToMany(fetch = FetchType.LAZY,
          cascade = {CascadeType.ALL}
  )
  @JsonIgnore
  private Set<Role> roles = new HashSet<>();

  @OneToMany(mappedBy = "liker",
          fetch = FetchType.LAZY,
          cascade = CascadeType.REMOVE,
          orphanRemoval = true
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnoreProperties(value = "liker")
  @JsonIgnore
  private Set<Like>liked_posts = new HashSet<>();



  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "profile_id", referencedColumnName = "id")
  @OnDelete(action=OnDeleteAction.CASCADE)
  @JsonIgnoreProperties(value = "user")
  @JsonIgnore
  private Profile profile;



  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

}