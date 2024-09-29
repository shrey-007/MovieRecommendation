package com.MoviesRecommender.moviesModule.helper;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.repository.MovieRepository;
import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.entity.UserWatchesMovie;
import com.MoviesRecommender.userModule.repository.UserWatchesMovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class UpdateDashboard {
    @Autowired
    private UserWatchesMovieRepository userWatchesMovieRepository;
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRecommender movieRecommender;
    public List<Movie> dashboard2(User user){
        log.info("Helper method for getting dashboard is calles for the user {}",user);

        // get the user
        log.info("Fetching movies watched by the user {} ",user);

        // First we will fetch all movies watched by the user earlier
        List<UserWatchesMovie> titleOfMoviesWatchedByUser = userWatchesMovieRepository.findAllByUserId(user.getId());

        // reverse the order, coz jo movie latest dekhi uski recommendation pehle dikhani hai, but voh arraylist mai last
        // mai hai toh last mai dikhegi uski recommendation, but apan ko pehle dikhani hai toh reverse krdo
        Collections.reverse(titleOfMoviesWatchedByUser);

        log.info("Fetching title of the movies watched by the user {} -> {} ",user,titleOfMoviesWatchedByUser);

        // Now fetch the movies, corresponding to the title
        List<Movie> movieList=new ArrayList<>();
        for(UserWatchesMovie userWatchesMovie:titleOfMoviesWatchedByUser){
            Movie movie=movieRepository.findByTitle(userWatchesMovie.getMovieTitle());
            movieList.add(movie);
        }
        log.info("Fetching movies from title for the user {} ",user);

        // fetch the movies which are similar to the list of movies which are already watched by user
        // Also they should be unique, same movie repeat nhi honi chaiye
        // Also order change nhi hona movie ka coz last watched movie se similar movie pehle aani chaiye
        Set<Movie> similarMovies=new LinkedHashSet<>();
        for (Movie movie:movieList){
            // find the similar movies
            List<Movie> similarMoviesToCurrentMovie=movieRecommender.recommendMovies(movie.getTitle());
            // add it to the final list
            similarMovies.addAll(similarMoviesToCurrentMovie);
        }
        log.info("Fetched the similar movies for the user {} -> {}",user,similarMovies);

        List<Movie> uniqueSimilarMovies = new ArrayList<>(similarMovies);

        // if the size of the list, is very big then we will remove some movies
        if(uniqueSimilarMovies.size()>50){
            int sizeOfList=uniqueSimilarMovies.size();
            Random random = new Random();
            while (sizeOfList>50){
                int randomIndex = random.nextInt(sizeOfList);
                uniqueSimilarMovies.remove(randomIndex);
                sizeOfList--;
            }
        }

        // if the size of the list is smaller than 50 , then show the top remaining rows from database
        else if(uniqueSimilarMovies.size()<50){
            int remainingSize=50-uniqueSimilarMovies.size();
            Pageable limit = PageRequest.of(0,remainingSize);
            Page<Movie> remainingMoviesPage = movieRepository.findAll(limit);
            List<Movie> remainingMovies = remainingMoviesPage.stream().toList();
            uniqueSimilarMovies.addAll(remainingMovies);
        }

        // Since yaha uper extra movies add kri hai toh duplicates aa skte hai
        Set<Movie> finalUniqueMovies = new LinkedHashSet<>(uniqueSimilarMovies);
        uniqueSimilarMovies = new ArrayList<>(finalUniqueMovies);


        return uniqueSimilarMovies;
    }
}
