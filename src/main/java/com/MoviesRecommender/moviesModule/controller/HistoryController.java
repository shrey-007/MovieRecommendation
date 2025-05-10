package com.MoviesRecommender.moviesModule.controller;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.entity.UserDashboard;
import com.MoviesRecommender.moviesModule.helper.UpdateDashboard;
import com.MoviesRecommender.moviesModule.repository.UserDashboardRepository;
import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.entity.UserWatchesMovie;
import com.MoviesRecommender.userModule.repository.UserWatchesMovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class HistoryController {

    @Autowired
    UserWatchesMovieRepository userWatchesMovieRepository;

    @Autowired
    UserDashboardRepository userDashboardRepository;

    @Autowired
    UpdateDashboard updateDashboard;
    @RequestMapping("/movies/updateWatchList")
    public String updateWatchListOfUser(@RequestParam String title, Model model){

        User user=(User) model.getAttribute("user");

        log.info("User {} has watched the movie {} more than 90%, so saving the movie ",user,title);

        UserWatchesMovie userWatchesMovie = new UserWatchesMovie(user.getId(),title);

        userWatchesMovieRepository.save(userWatchesMovie);

        log.info("Watch list of the user {} has been updated",user);

        //update the dashboard of the user
        log.info("updating the dashboard of the user by calling the helper method");
        List<Movie> movies = updateDashboard.dashboard2(user);
        userDashboardRepository.save(new UserDashboard(user.getId(),movies));

        return "redirect:/movies/movie?title="+title;
    }
}
