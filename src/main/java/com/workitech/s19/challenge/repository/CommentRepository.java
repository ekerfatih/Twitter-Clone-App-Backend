package com.workitech.s19.challenge.repository;

import com.workitech.s19.challenge.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
           select c from Comment c
           join fetch c.user cu
           join fetch c.tweet t
           join fetch t.user tu
           where t.id = :tweetId
           order by c.time desc
           """)
    List<Comment> getCommentsWithPostId(@Param("tweetId") Long tweetId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from Comment c where c.id = :id")
    void hardDeleteById(@Param("id") Long id);
}
