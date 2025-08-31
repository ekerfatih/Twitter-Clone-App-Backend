package com.workitech.s19.challenge.repository;

import com.workitech.s19.challenge.entity.*;
import com.workitech.s19.challenge.exceptions.TwitterNotFoundException;
import jakarta.persistence.EntityManager;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class RepositoryTests {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    TweetRetweetRepository tweetRetweetRepository;
    @Autowired
    TweetLikeRepository tweetLikeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TweetRepository tweetRepository;
    @Autowired
    RoleRepository roleRepository;

    User user;
    Role role;

    @BeforeEach
    void setUp() {
        role = roleRepository.save(new Role(null, "USER"));
        user = userRepository.save(new User(null, "feker", "f@f", "123", "fatih", "eker",
                role, new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    @DisplayName("List all comments for given tweetID")
    void getCommentsWithPostId_returnsPersistedComments() {
        Tweet tweet = tweetRepository.save(new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        tweet.addComment(commentRepository.save(new Comment(null, "first comment", tweet, LocalDateTime.now(), user)));
        tweet.addComment(commentRepository.save(new Comment(null, "second comment", tweet, LocalDateTime.now(), user)));

        List<Comment> result = commentRepository.getCommentsWithPostId(tweet.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Comment::getCommentText)
                .containsExactlyInAnyOrder("first comment", "second comment");
        assertThat(result).allMatch(c -> c.getTweet().getId().equals(tweet.getId()));
    }

    @Test
    @DisplayName("Create comment")
    void createCommentTest() {
        Tweet tweet = tweetRepository.save(new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        Comment beforeSave = new Comment(null, "third comment", tweet, LocalDateTime.now(), user);
        Comment saved = tweet.addComment(commentRepository.save(beforeSave));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCommentText()).isEqualTo("third comment");
    }

    @Test
    @DisplayName("Update Comment")
    void updateCommentTest() {

        Tweet tweet = tweetRepository.save(new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        Comment saved = tweet.addComment(commentRepository.save(
                new Comment(null, "omaewa mou shindeiru", tweet, LocalDateTime.now(), user)
        ));
        Long id = saved.getId();
        saved.setCommentText("Naniiii");
        commentRepository.save(saved);
        Comment reloaded = commentRepository.findById(id).orElseThrow();
        assertThat(reloaded.getCommentText()).isEqualTo("Naniiii");
    }

    @Test
    @DisplayName("Delete Comment")
    void deleteCommentTest() {

        Tweet tweet = tweetRepository.save(new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        Comment saved = tweet.addComment(
                commentRepository.save(
                        new Comment(null, "wabalaba dub dub", tweet, LocalDateTime.now(), user)
                )
        );
        long id = saved.getId();
        Comment deleteComment = commentRepository.findById(id).orElseThrow(() -> new TwitterNotFoundException("Cant find it"));
        commentRepository.delete(deleteComment);
        Comment deleted = commentRepository.findById(id).orElse(null);
        assertNull(deleted);
    }

    @Test
    @DisplayName("Hard Delete Comment")
    void hardDeleteCommentTest() {

        Tweet tweet = tweetRepository.save(new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        Comment saved = tweet.addComment(
                commentRepository.save(
                        new Comment(null, "wabalaba dub dub", tweet, LocalDateTime.now(), user)
                )
        );
        long id = saved.getId();
        commentRepository.hardDeleteById(id);
        Comment deleted = commentRepository.findById(id).orElse(null);
        assertNull(deleted);
    }

    @Test
    @DisplayName("Find by Role name")
    void findByAuthority() {

        Optional<Role> role = roleRepository.findByAuthority("USER");
        if (role.isPresent()) {
            assertEquals("USER", role.get().getAuthority());
        } else {
            throw new TwitterNotFoundException("Expected role not in the db");
        }
    }

    @Test
    @DisplayName("Throw error while finding a role doesn't exist")
    void doNotFindByAuthority() {
        Optional<Role> role = roleRepository.findByAuthority("MEGASTAR");
        assertThat(role).isNotPresent();
    }

    @Test
    @DisplayName("Check if a tweet likable")
    void likeTweetTest() {

        Tweet tweet = tweetRepository.save(new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        TweetLike tweetLike = new TweetLike(null, tweet, user, LocalDateTime.now());

        TweetLike tls = tweetLikeRepository.save(tweetLike);

        assertThat(tls.getId()).isNotNull();
        assertThat(tls.getUser()).isNotNull();
        assertThat(tls.getTweet()).isNotNull();
        assertThat(tls.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Check given tweet id has like by current user")
    void checkByTweetIdHasLikeByCurrentUser() {

        Tweet tweet = tweetRepository.save(new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        TweetLike tweetLike = new TweetLike(null, tweet, user, LocalDateTime.now());
        TweetLike tls = tweetLikeRepository.save(tweetLike);
        boolean isExistLike = tweetLikeRepository.existsByTweetIdAndUserId(tweet.getId(), user.getId());
        assertThat(isExistLike).isTrue();
    }

    @Test
    @DisplayName("Is given like count true?")
    void checkLikeCountTest() {
        Tweet tweet = tweetRepository.save(new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));


        User user2 = userRepository.save(new User(null, "newuser", "new@new", "123", "new", "user", role, new ArrayList<>(), new ArrayList<>()));
        TweetLike tweetLike = new TweetLike(null, tweet, user, LocalDateTime.now());
        TweetLike tweetLike2 = new TweetLike(null, tweet, user2, LocalDateTime.now());
        TweetLike tls = tweetLikeRepository.save(tweetLike);
        TweetLike tls2 = tweetLikeRepository.save(tweetLike2);

        long count = tweetLikeRepository.countByTweetId(tweet.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Unlike functionality")
    void canUserUnlikeTweetTest() {

        Tweet tweet = tweetRepository.save(new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        User user2 = userRepository.save(new User(null, "newuser", "new@new", "123", "new", "user", role, new ArrayList<>(), new ArrayList<>()));
        TweetLike tweetLike = new TweetLike(null, tweet, user, LocalDateTime.now());
        TweetLike tweetLike2 = new TweetLike(null, tweet, user2, LocalDateTime.now());
        TweetLike tls = tweetLikeRepository.save(tweetLike);
        TweetLike tls2 = tweetLikeRepository.save(tweetLike2);
        long count = tweetLikeRepository.countByTweetId(tweet.getId());
        assertThat(count).isEqualTo(2);

        tweetLikeRepository.deleteByTweetAndUser(tweet.getId(), user2.getId());
        count = tweetLikeRepository.countByTweetId(tweet.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Test for getting all tweets by username")
    void testForGettingTweetsByUsername() {
        User user2 = userRepository.save(new User(null, "fe", "e@e", "123", "fatih", "eker",
                role, new ArrayList<>(), new ArrayList<>()));
        Tweet tweet1 = tweetRepository.save(
                new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>())
        );

        Tweet tweet2 = tweetRepository.save(
                new Tweet(null, "Mock2 tweet", user, LocalDateTime.now(),
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>())
        );

        Tweet tweet3 = tweetRepository.save(
                new Tweet(null, "Mock3 tweet", user2, LocalDateTime.now(),
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>())
        );

        List<Tweet> list = tweetRepository.getAllByUser(user2.getUsername());

        assertThat(list.size()).isEqualTo(1);
        assertThat(list).isNotNull();
        assertThat(list).contains(tweet3);
    }

    @Test
    @DisplayName("Get Feed working properly")
    void FeedTest() {
        Tweet tweet = tweetRepository.save(
                new Tweet(null, "Mock tweet", user, LocalDateTime.now(),
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>())
        );
        Tweet tweet2 = tweetRepository.save(
                new Tweet(null, "Mock2 tweet", user, LocalDateTime.now(),
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>())
        );

        List<Tweet> tweets = tweetRepository.getAllByTimeDESC();
        assertThat(tweets).isNotNull();
        assertThat(tweets.size()).isEqualTo(2);
    }

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("Tweet creation → commenting → cascade delete works")
    void tweet_comment_lifecycle() {
        Tweet t = tweetRepository.save(new Tweet(null, "first tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));


        Comment c = t.addComment(commentRepository.save(new Comment(null, "first comment", t, LocalDateTime.now(), user)));
        Comment c1 = t.addComment(commentRepository.save(new Comment(null, "second comment", t, LocalDateTime.now(), user)));

        long count = commentRepository.getCommentsWithPostId(t.getId()).size();
        assertThat(count).isEqualTo(2);

        t.removeComment(c);
        tweetRepository.save(t);

        assertThat(t.getComments().size()).isEqualTo(1);
        assertThat(t.getComments().stream().findFirst().get().getCommentText()).isEqualTo("second comment");
    }

    @Test
    @DisplayName("Test finding user by username")
    void findingUserByUsername() {
        User u = userRepository.findUserByUsername("feker")
                .orElseThrow(() -> new TwitterNotFoundException("not found"));
        assertThat(u).isNotNull();
    }

    @Test
    @DisplayName("Test finding non existing user by username")
    void findingNonExistingUser() {
        User u = userRepository.findUserByUsername("fff")
                .orElse(null);
        assertThat(u).isNull();
    }

    @Test
    @DisplayName("Find an user by email")
    void findingUserByEmail() {
        User u = userRepository.findUserByEmail("f@f")
                .orElse(null);
        assertThat(u).isNotNull();
    }

    @Test
    @DisplayName("Exist by methods working")
    void existByUsernameAndEmailWorking() {
        boolean existByUsername = userRepository.existsByUsername("feker");
        boolean existByEmail = userRepository.existsByEmail("f@f");
        boolean nonExistByUsername = userRepository.existsByUsername("llll");
        boolean nonExistByEmail = userRepository.existsByEmail("l@l");
        assertThat(existByUsername).isTrue();
        assertThat(existByEmail).isTrue();
        assertThat(nonExistByUsername).isFalse();
        assertThat(nonExistByEmail).isFalse();
    }

    @Test
    @DisplayName("Test retweet count functionalty")
    void testRetweetCount() {
        User user2 = userRepository.save(new User(null, "fff", "l@l", "123", "fatih", "eker",
                role, new ArrayList<>(), new ArrayList<>()));

        Tweet t = tweetRepository.save(new Tweet(null, "first tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        tweetRetweetRepository.save(new TweetRetweet(null, t, user, LocalDateTime.now()));
        tweetRetweetRepository.save(new TweetRetweet(null, t, user2, LocalDateTime.now()));
        long count = tweetRetweetRepository.countByTweetId(t.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Test cancel retweet delete")
    void deleteRetweetFunction(){
        Tweet t = tweetRepository.save(new Tweet(null, "first tweet", user, LocalDateTime.now(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        tweetRetweetRepository.save(new TweetRetweet(null, t, user, LocalDateTime.now()));

        tweetRetweetRepository.deleteByTweetAndUser(t.getId(), user.getId());

        long count = tweetRetweetRepository.countByTweetId(t.getId());
        assertThat(count).isEqualTo(0);

    }

}

