package com.MoviesRecommender.moviesModule.controller;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.helper.MovieRecommender;
import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.entity.UserWatchesMovie;
import com.MoviesRecommender.userModule.repository.UserWatchesMovieRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/movies")
public class MoviesController {
    @Autowired
    private MovieRecommender movieRecommender;

    @Autowired
    private UserWatchesMovieRepository userWatchesMovieRepository;

    @GetMapping("/movie")
    public String movie(@RequestParam String title, Model model) {
        // get the string of recommended movies
        List<Movie> similarMoviesTitle=movieRecommender.recommendMovies(title);

        model.addAttribute("title",title);
        model.addAttribute("similarMovies",similarMoviesTitle);
        return "movie";
    }

    @RequestMapping("/updateWatchList")
    public String updateWatchListOfUser(@RequestParam String title, HttpSession session){
        System.out.println(title+"=====================");

        User user=(User) session.getAttribute("user");

        UserWatchesMovie userWatchesMovie = new UserWatchesMovie(user.getId(),title);

        userWatchesMovieRepository.save(userWatchesMovie);

        return "redirect:/movies/movie?title="+title;
    }


}
