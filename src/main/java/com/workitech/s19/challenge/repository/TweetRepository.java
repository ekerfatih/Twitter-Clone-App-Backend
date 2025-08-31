package com.workitech.s19.challenge.repository;

import com.workitech.s19.challenge.entity.Tweet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    @Query("SELECT t FROM Tweet t WHERE t.user.username = :username ORDER BY t.time DESC")
    List<Tweet> getAllByUser(String username);

    @Query("SELECT t FROM Tweet t ORDER BY t.time DESC")
    List<Tweet> getAllByTimeDESC();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Tweet t where t.id = :id")
    @Transactional
    void hardDeleteById(@Param("id") Long id);

}
