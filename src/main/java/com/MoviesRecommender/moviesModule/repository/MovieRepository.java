package com.MoviesRecommender.moviesModule.repository;

import com.MoviesRecommender.moviesModule.entity.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {

    Movie findByTitle(String title);

    List<Movie> findByTitleContaining(String searchQuery);

}
