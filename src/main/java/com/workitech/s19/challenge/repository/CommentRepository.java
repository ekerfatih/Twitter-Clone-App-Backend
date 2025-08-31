package com.workitech.s19.challenge.repository;

import com.workitech.s19.challenge.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.tweet.id = :tweetId ORDER BY c.time DESC")
    List<Comment> getCommentsWithPostId(@Param("tweetId") Long tweetId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Comment c where c.id = :id")
    void hardDeleteById(@Param("id") Long id);
}
