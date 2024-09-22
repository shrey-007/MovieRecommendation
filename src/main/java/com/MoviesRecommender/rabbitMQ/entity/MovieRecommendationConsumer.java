package com.MoviesRecommender.rabbitMQ.entity;

import com.MoviesRecommender.moviesModule.repository.MovieRepository;
import com.MoviesRecommender.rabbitMQ.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.entity.MovieRecommendationsResponse;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MovieRecommendationConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MovieRepository movieRepository;

    // In-memory storage to hold the results temporarily
    private ConcurrentHashMap<String, List<Movie>> recommendationsCache = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMovieRecommendationRequest(MovieRecommendationMessage message) {
        log.info("Consumer received the message {} form queue ", message);
        String movieTitle = message.getMovieTitle();
        String requestId = message.getRequestId();

        log.info("Received asynchronous request to get recommendations for movie: {}", movieTitle);

        // Call the Python API
        String pythonApiUrl = "http://127.0.0.1:5000/recommend?movie=" + movieTitle;
        MovieRecommendationsResponse movieRecommendationsResponse = restTemplate.getForObject(pythonApiUrl, MovieRecommendationsResponse.class);

        log.info("Received recommendation response for movie {}: {}", movieTitle, movieRecommendationsResponse);

        // Get the movies from the title
        List<Movie> movies = new ArrayList<>();
        for (String title : movieRecommendationsResponse.getRecommendations()) {
            Movie movie = movieRepository.findByTitle(title);
            movies.add(movie);
        }

        // store only unique movies
        Set<Movie> uniquesMovies = new HashSet<>(movies);
        movies = new ArrayList<>(uniquesMovies);

        // Get the current Movie
        Movie currentMovie = movieRepository.findByTitle(message.getMovieTitle());

        // Avatar ke recommendation mai Avatar khud ni aani chaiye
        Iterator<Movie> iterator = movies.iterator();
        while (iterator.hasNext()) {
            Movie movie = iterator.next();
            if (movie.getTitle().equals(currentMovie.getTitle())) {
                iterator.remove();
            }
        }

        // Store the result in the cache
        recommendationsCache.put(requestId, movies);
        log.info("Stored recommendations for requestId: {} in the cache, below are the recommendations {}", requestId,getRecommendationResult(requestId));
    }

    // Method to get the result from the cache
    public List<Movie> getRecommendationResult(String requestId) {
        return recommendationsCache.get(requestId);
    }
}


