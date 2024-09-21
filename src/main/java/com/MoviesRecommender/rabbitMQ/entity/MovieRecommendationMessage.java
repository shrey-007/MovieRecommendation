package com.MoviesRecommender.rabbitMQ.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Data
public class MovieRecommendationMessage {
    private String movieTitle;
    private String requestId;

    // Constructors, getters, and setters
}

