package com.MoviesRecommender.moviesModule.controller;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.helper.MovieRecommender;
import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.entity.UserWatchesMovie;
import com.MoviesRecommender.userModule.repository.UserWatchesMovieRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/movies")
@Slf4j
public class MoviesController {
    @Autowired
    private MovieRecommender movieRecommender;

    @Autowired
    private UserWatchesMovieRepository userWatchesMovieRepository;

    @GetMapping("/movie")
    public String movie(@RequestParam String title, Model model) {

        log.info(" User requested for the recommendation of movie {} ", title);
        // get the string of recommended movies
        List<Movie> similarMoviesTitle=movieRecommender.recommendMovies(title);
        log.info(" Got recommendation of movie {} ", title);

        model.addAttribute("title",title);
        model.addAttribute("similarMovies",similarMoviesTitle);
        return "movie";
    }

    @RequestMapping("/updateWatchList")
    public String updateWatchListOfUser(@RequestParam String title, HttpSession session){

        User user=(User) session.getAttribute("user");

        UserWatchesMovie userWatchesMovie = new UserWatchesMovie(user.getId(),title);

        userWatchesMovieRepository.save(userWatchesMovie);

        log.info("Watch list of the user {} has been updated",user);

        return "redirect:/movies/movie?title="+title;
    }


}
