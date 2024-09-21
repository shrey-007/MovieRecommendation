package com.MoviesRecommender.rabbitMQ.helper;

import com.MoviesRecommender.RabbitMQ.entity.MovieRecommendationConsumer;
import com.MoviesRecommender.moviesModule.entity.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class APIRequestStatus {

    @Autowired
    MovieRecommendationConsumer movieRecommendationConsumer;

    @RequestMapping("/movie/status")
    public ResponseEntity<?> getMovieRecommendationsStatus(@RequestParam String requestId){
        log.info("API status is fetched at /movie/status for the requestId {} ",requestId);

        List<Movie> similarMovies = movieRecommendationConsumer.getRecommendationResult(requestId);

        if(similarMovies==null){
            log.info(" Still not processed ");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // No content means still processing;
        }
        else{
            log.info(" Processed, returning the movies ");
            return ResponseEntity.ok(similarMovies);
        }
    }

}
