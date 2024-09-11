package com.MoviesRecommender.moviesModule.controller;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.repository.MovieRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class SearchController {

    @Autowired
    MovieRepository movieRepository;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Model model){

        log.info("User Searched for movies {} ",query);

        //fetch list of questions to be displayed on search bar, according to query
        List<Movie> movieList = movieRepository.findByTitleContaining(query);

        log.info("Search Results: {} ",movieList);

        //yaha se serealize hokr questions pahuch jaaege
        return ResponseEntity.ok(movieList);
    }

}
