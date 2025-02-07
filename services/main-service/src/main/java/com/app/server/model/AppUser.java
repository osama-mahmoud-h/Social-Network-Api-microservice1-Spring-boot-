package com.app.server.model;

import com.app.server.enums.UserRole;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.*;

import  jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUser implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long userId;

  @Column(nullable = false)
  private String firstName;

  private String lastName;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column()
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole userRole ;

  @Column(nullable = false)
  private Instant createdAt;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "user_friends",
          joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId", foreignKey = @ForeignKey(name = "FK_user_friends_user_id")),
          inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "userId", foreignKey = @ForeignKey(name = "FK_user_friends_friend_id"))
  )
  private Set<AppUser> friends;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+this.userRole.name()));
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
