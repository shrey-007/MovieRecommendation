package com.MoviesRecommender.moviesModule.controller;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.helper.MovieRecommender;
import com.MoviesRecommender.moviesModule.repository.MovieRepository;
import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.entity.UserWatchesMovie;
import com.MoviesRecommender.userModule.repository.UserWatchesMovieRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class UserController {
    @Autowired
    MovieRepository movieRepository;

    @Autowired
    UserWatchesMovieRepository userWatchesMovieRepository;

    @Autowired
    MovieRecommender movieRecommender;

    @RequestMapping("/dashboard")
    public String dashboard(Model model){

        // get the user
        User user=(User) model.getAttribute("user");
        log.info("Fetching movies watched by the user {} ",user);

        // First we will fetch all movies watched by the user earlier
        List<UserWatchesMovie> titleOfMoviesWatchedByUser = userWatchesMovieRepository.findAllByUserId(user.getId());

        // reverse the order, coz jo movie latest dekhi uski recommendation pehle dikhani hai, but voh arraylist mai last
        // mai hai toh last mai dikhegi uski recommendation, but apan ko pehle dikhani hai toh reverse krdo
        List<UserWatchesMovie> reveserOfTitleOfMoviesWatchedByUser=new ArrayList<>();
        for (int i = titleOfMoviesWatchedByUser.size()-1; i >=0 ; i--) {
            reveserOfTitleOfMoviesWatchedByUser.add(titleOfMoviesWatchedByUser.get(i));
        }
        titleOfMoviesWatchedByUser=reveserOfTitleOfMoviesWatchedByUser;

        log.info("Fetching title of the movies watched by the user {} -> {} ",user,titleOfMoviesWatchedByUser);

        // Now fetch the movies, corresponding to the title
        List<Movie> movieList=new ArrayList<>();
        for(UserWatchesMovie userWatchesMovie:titleOfMoviesWatchedByUser){
            Movie movie=movieRepository.findByTitle(userWatchesMovie.getMovieTitle());
            movieList.add(movie);
        }
        log.info("Fetching movies from title for the user {} ",user);

        // fetch the movies which are similar to the list of movies which are already watched by user
        List<Movie> similarMovies=new ArrayList<>();
        for (Movie movie:movieList){
            // find the similar movies
            List<Movie> similarMoviesToCurrentMovie=movieRecommender.recommendMovies(movie.getTitle());
            // add it to the final list
            similarMovies.addAll(similarMoviesToCurrentMovie);
        }
        log.info("Fetched the similar movies for the user {} -> {}",user,similarMovies);

        // if the size of the list, is very big then we will remove some movies
        if(similarMovies.size()>50){
            int sizeOfList=similarMovies.size();
            Random random = new Random();
            while (sizeOfList>50){
                int randomIndex = random.nextInt(sizeOfList);
                similarMovies.remove(randomIndex);
                sizeOfList--;
            }
        }

        // if the size of the list is smaller than 50 , then show the top remaining rows from database
        else if(similarMovies.size()<50){
            int remainingSize=50-similarMovies.size();
            Pageable limit = PageRequest.of(0,remainingSize);
            Page<Movie> remainingMoviesPage = movieRepository.findAll(limit);
            List<Movie> remainingMovies = remainingMoviesPage.stream().toList();
            similarMovies.addAll(remainingMovies);
        }

        model.addAttribute("moviesList",similarMovies);

        return "dashboard";
    }

}
