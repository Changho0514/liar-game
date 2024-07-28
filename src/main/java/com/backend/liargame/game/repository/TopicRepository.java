package com.backend.liargame.game.repository;

import com.backend.liargame.game.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
