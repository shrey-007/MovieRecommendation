package com.MoviesRecommender.moviesModule.helper;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.entity.MovieRecommendationsResponse;
import com.MoviesRecommender.moviesModule.repository.MovieRepository;
import com.MoviesRecommender.rabbitMQ.entity.MovieRecommendationProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class MovieRecommender {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRecommendationProducer movieRecommendationProducer;

    public List<Movie> recommendMovies(String movieTitle) {
        log.info("Calling the Python API to get similar movies to {} ",movieTitle);

        String pythonApiUrl = "http://127.0.0.1:5000/recommend?movie=" + movieTitle;

        // Call the Python API
        MovieRecommendationsResponse movieRecommendationsResponse = restTemplate.getForObject(pythonApiUrl, MovieRecommendationsResponse.class);

        log.info("got the recommendation of the movie {} -> {} ",movieTitle,movieRecommendationsResponse);

        // Get the movies from the title
        List<Movie> movies = new ArrayList<>();
        for (String title: movieRecommendationsResponse.getRecommendations()){
            if(title.equals(null) || title.equals("")){continue;}
            Movie movie = movieRepository.findByTitle(title);
            movies.add(movie);
        }

        log.info("got the similar movies of the movie {}",movieTitle);

        // return the List<String>, i.e title of similar movies
        return movies;
    }

    public String recommendMoviesAsync(String movieTitle) {
        log.info("MovieRecommender method recommendMoviesAsync is called");
        // Generate a unique request ID
        String requestId = UUID.randomUUID().toString();
        log.info("Sending asynchronous request to get recommendations for movie: {} with requestId {}", movieTitle, requestId);

        // Send the movie title and requestId to the RabbitMQ queue
        movieRecommendationProducer.sendMovieRecommendationRequest(movieTitle, requestId);
        return requestId;  // Return the request ID to the caller
    }


}
