package com.backend.liargame.game.service;

import com.backend.liargame.game.dto.TopicDTO;
import com.backend.liargame.game.entity.Topic;
import com.backend.liargame.game.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<TopicDTO> getAllTopics(){
        return topicRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    };

    public TopicDTO getTopicById(Long id) {
        return topicRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public void saveTopic(String name){
        Topic topic = new Topic(name);
        topicRepository.save(topic);
    }

    public void deleteTopic(Long id){
        topicRepository.deleteById(id);
    }

    private TopicDTO convertToDTO(Topic topic) {
        return new TopicDTO(topic.getId(), topic.getName());
    }
}
