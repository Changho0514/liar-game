package com.backend.liargame.game.contoller;

import com.backend.liargame.game.dto.TopicDTO;
import com.backend.liargame.game.entity.Topic;
import com.backend.liargame.game.service.TopicService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {


    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public List<TopicDTO> getAllTopics(){
        return topicService.getAllTopics();
    }



    @GetMapping("/{id}")
    public TopicDTO getTopicById(@PathVariable Long id) {
        return topicService.getTopicById(id);
    }

    @PostMapping
    public void createTopic(@RequestBody String name){
        topicService.saveTopic(name);

    }

    @DeleteMapping("/{id}")
    public void deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
    }

}
