package com.MoviesRecommender.moviesModule.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MovieRecommendationsResponse {
    @JsonProperty("recommendations")
    private List<String> recommendations;

}
