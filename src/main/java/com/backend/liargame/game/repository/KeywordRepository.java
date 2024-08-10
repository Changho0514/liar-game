package com.backend.liargame.game.repository;

import com.backend.liargame.game.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    @Query(value = "SELECT * FROM keyword WHERE topic_id = ?1 ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Keyword> findRandomKeywordByTopicId(Long topicId);

    @Query(value = "SELECT keyword.name FROM keyword WHERE topic_id = ?1 AND keyword.name <> ?2 ORDER BY RAND() LIMIT 14", nativeQuery = true)
    List<String> findOptionKeywordByTopicIdExcludingKeyword(Long topicId, String keyword);

}
