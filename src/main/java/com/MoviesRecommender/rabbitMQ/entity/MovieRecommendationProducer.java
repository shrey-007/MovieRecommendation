package com.MoviesRecommender.rabbitMQ.entity;

import com.MoviesRecommender.rabbitMQ.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MovieRecommendationProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMovieRecommendationRequest(String movieTitle, String requestId) {
        log.info("Producer is called");
        // Combine movie title and request ID into a message object
        MovieRecommendationMessage message = new MovieRecommendationMessage(movieTitle, requestId);

        // Send the message to the RabbitMQ queue
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
        log.info("This message is sent to the queue {} ",message);
    }
}


