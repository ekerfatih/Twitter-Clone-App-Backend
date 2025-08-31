package com.workitech.s19.challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"tweets", "comments", "role"})
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "users", schema = "twitter")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 20)
    @Column(name = "username", length = 20, unique = true, nullable = false)
    @NotBlank
    private String username;

    @Email
    @Size(max = 40)
    @Column(name = "email", length = 40, unique = true, nullable = false)
    @NotBlank
    private String email;

    @Size(max = 100)
    @Column(name = "hashed_password", length = 100, nullable = false)
    @NotBlank
    private String password;

    @Size(max = 20)
    @Column(name = "first_name", length = 20, nullable = false)
    @NotBlank
    private String firstName;

    @Size(max = 20)
    @Column(name = "last_name", length = 20, nullable = false)
    @NotBlank
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Tweet> tweets = new ArrayList<>();

    public List<Tweet> getTweets() {
        return Collections.unmodifiableList(tweets);
    }

    public void addTweet(Tweet tweet) {
        tweets.add(tweet);
    }

    public void removeTweet(Tweet tweet) {
        tweets.remove(tweet);
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();


    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
