package com.MoviesRecommender.moviesModule.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
// When the response from python API, will come it will be in JSON format, so we have to convert it into the java class
// So this is the reason of creating this class
public class MovieRecommendationsResponse {
    @JsonProperty("recommendations")
    private List<String> recommendations;

}
