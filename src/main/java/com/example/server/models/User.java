package com.example.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

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
  private Set<PostLike>liked_posts = new HashSet<>();

  @OneToMany(mappedBy = "author",
          fetch = FetchType.LAZY,
          cascade = CascadeType.REMOVE,
          orphanRemoval = true
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnoreProperties(value = {"author"})
  @JsonIgnore
  Set<Post>posts = new HashSet<>();

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "profile_id", referencedColumnName = "id")
  @OnDelete(action=OnDeleteAction.CASCADE)
  @JsonIgnoreProperties(value = "user")
  @JsonIgnore
  private Profile profile;

  @OneToMany(mappedBy = "followed",
          fetch = FetchType.LAZY,
          cascade = CascadeType.REMOVE,
          orphanRemoval = true
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnoreProperties(value = {"followed","follower"})
  @JsonIgnore
  Set<Follower>followers = new HashSet<>();

  @OneToMany(mappedBy = "follower",
          fetch = FetchType.LAZY,
          cascade = CascadeType.REMOVE,
          orphanRemoval = true
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JsonIgnoreProperties(value = {"follower","followed"})
  @JsonIgnore
  Set<Follower>following = new HashSet<>();

  @Column(nullable = true,name = "timestamp")
  @CreationTimestamp
  private Timestamp timestamp;



  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public void addFollower(Follower follower){
    this.followers.add(follower);
  }
  public void addFollowing(Follower following){
    this.following.add(following);
  }


}
