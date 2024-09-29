package com.MoviesRecommender.moviesModule.service;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.entity.UserDashboard;
import com.MoviesRecommender.moviesModule.repository.UserDashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDashboardService {
    @Autowired
    private UserDashboardRepository userDashboardRepository;

    public List<Movie> getDashboardMovies(String userId){
        return userDashboardRepository.findByUserId(userId)
                .map(UserDashboard::getMovies).orElse(null);
    }

}
