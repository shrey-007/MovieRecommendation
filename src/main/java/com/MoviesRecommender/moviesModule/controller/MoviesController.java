package com.MoviesRecommender.moviesModule.controller;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.entity.UserDashboard;
import com.MoviesRecommender.moviesModule.helper.MovieRecommender;
import com.MoviesRecommender.moviesModule.repository.MovieRepository;
import com.MoviesRecommender.moviesModule.repository.UserDashboardRepository;
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

import java.util.*;

@Controller
@RequestMapping("/movies")
@Slf4j
public class MoviesController {

    @Autowired
    MovieRepository movieRepository;
    @Autowired
    private MovieRecommender movieRecommender;

    @Autowired
    private UserWatchesMovieRepository userWatchesMovieRepository;

    @Autowired
    private UserDashboardRepository userDashboardRepository;

    /** Replaced by Async call */
//    @GetMapping("/movie")
//    public String movie(@RequestParam String title, Model model) {
//
//        Movie currentMovie = movieRepository.findByTitle(title);
//
//        log.info(" User requested for the recommendation of movie {} ", currentMovie);
//
//        // get the recommended movies
//        List<Movie> similarMovies=movieRecommender.recommendMovies(title);
//        Set<Movie> uniqueSimilarMovies = new HashSet<>(similarMovies);
//        similarMovies = new ArrayList<>(uniqueSimilarMovies);
//
//        log.info(" Got recommendation of movie {} ", similarMovies);
//
//        // Avatar ke recommendation mai Avatar khud nhi dikhni chaiye
//        Iterator<Movie> iterator = similarMovies.iterator();
//        while (iterator.hasNext()) {
//            Movie movie = iterator.next();
//            if (movie.getTitle().equals(title)) {
//                iterator.remove();
//            }
//        }
//
//        log.info(" Removed duplicate movies {} ", similarMovies);
//
//        model.addAttribute("currentMovie",currentMovie);
//        model.addAttribute("similarMovies",similarMovies);
//        return "movie";
//    }

    @GetMapping("/movie")
    public String movie2(@RequestParam String title, Model model) {

        Movie currentMovie = movieRepository.findByTitle(title);

        log.info("Controller /movies/movie is called");

        log.info("User requested recommendation for movie: {}", title);

        // Trigger the asynchronous recommendation
        String requestId = movieRecommender.recommendMoviesAsync(title);

        log.info("Calling the movieRecommender and returning the empty webpage", title);
        model.addAttribute("currentMovie", currentMovie);
        model.addAttribute("requestId", requestId);
        return "movie";
    }


    @RequestMapping("/updateWatchList")
    public String updateWatchListOfUser(@RequestParam String title, Model model){

        User user=(User) model.getAttribute("user");

        UserWatchesMovie userWatchesMovie = new UserWatchesMovie(user.getId(),title);

        userWatchesMovieRepository.save(userWatchesMovie);

        log.info("Watch list of the user {} has been updated",user);

        Movie currentMovie = movieRepository.findByTitle(title);

        List<Movie> moviesTobeShownOnDashboard = userDashboardRepository.findByUserId(user.getId());
        Set<Movie> uniqueMoviesTobeShownOnDashboard = new LinkedHashSet<>(moviesTobeShownOnDashboard);
        uniqueMoviesTobeShownOnDashboard.add(currentMovie);

        moviesTobeShownOnDashboard = new ArrayList<>(uniqueMoviesTobeShownOnDashboard);

        userDashboardRepository.save(new UserDashboard(user.getId(),moviesTobeShownOnDashboard));

        return "redirect:/movies/movie?title="+title;
    }


}
