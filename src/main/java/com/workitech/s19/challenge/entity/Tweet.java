package com.workitech.s19.challenge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"user", "comments"})
@EqualsAndHashCode(of = "id")

@Entity
@Table(name = "tweet", schema = "twitter")
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 300)
    @Column(name = "tweet_text", nullable = false)
    private String tweetText;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime time;

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public Comment addComment(Comment comment) {
        comment.setTweet(this);
        comments.add(comment);
        return comment;
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setTweet(null);
    }

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TweetLike> likes = new ArrayList<>();

    public boolean isLikedBy(User user) {
        if (user == null || likes == null) return false;
        Long uid = user.getId();
        if (uid == null) return false;
        return likes.stream().anyMatch(tl -> {
            User u = tl.getUser();
            return u != null && uid.equals(u.getId());
        });
    }


    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TweetRetweet> retweets = new ArrayList<>();

    public boolean isRetweetedBy(User user) {
        if (user == null || retweets == null) return false;
        Long uid = user.getId();
        if (uid == null) return false;
        return retweets.stream().anyMatch(tr -> {
            User u = tr.getUser();
            return u != null && uid.equals(u.getId());
        });
    }

    public int getLikeCount() { return likes == null ? 0 : likes.size(); }
    public int getRetweetCount() { return retweets == null ? 0 : retweets.size(); }
}
