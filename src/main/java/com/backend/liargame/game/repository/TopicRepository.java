package com.backend.liargame.game.repository;

import com.backend.liargame.game.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    @Query(value = "SELECT * FROM topic ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Topic> findRandomTopic();
}
