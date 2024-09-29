package com.MoviesRecommender.moviesModule.repository;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.entity.UserDashboard;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserDashboardRepository extends MongoRepository<UserDashboard,String> {
    public Optional<UserDashboard> findByUserId(String userId);
}
