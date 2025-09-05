package com.workitech.s19.challenge.repository;

import com.workitech.s19.challenge.entity.Tweet;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    @EntityGraph(attributePaths = {"user"})
    @Query("select t from Tweet t where t.user.username = :username order by t.time desc")
    List<Tweet> getAllByUser(@Param("username") String username);

    @EntityGraph(attributePaths = {"user"})
    @Query("select t from Tweet t order by t.time desc")
    List<Tweet> getAllByTimeDESC();


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Tweet t where t.id = :id")
    @Transactional
    void hardDeleteById(@Param("id") Long id);
}
