package com.MoviesRecommender.moviesModule.helper;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.entity.MovieRecommendationsResponse;
import com.MoviesRecommender.moviesModule.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class MovieRecommender {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> recommendMovies(String movieTitle) {
        String pythonApiUrl = "http://127.0.0.1:5000/recommend?movie=" + movieTitle;

        // Call the Python API
        MovieRecommendationsResponse movieRecommendationsResponse = restTemplate.getForObject(pythonApiUrl, MovieRecommendationsResponse.class);

        System.out.println("got the recommendation of the movie "+movieTitle+" ---->"+movieRecommendationsResponse+"------>");

        // Get the movies from the title
        List<Movie> movies = new ArrayList<>();
        for (String title: movieRecommendationsResponse.getRecommendations()){
            Movie movie = movieRepository.findByTitle(title);
            movies.add(movie);
        }

        System.out.println("got the movies of the movie "+movieTitle+" ---->"+movies+"------>");

        // return the List<String>, i.e title of similar movies
        return movies;
    }

}
