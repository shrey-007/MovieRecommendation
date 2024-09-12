package com.MoviesRecommender.moviesModule.controller;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.helper.MovieRecommender;
import com.MoviesRecommender.moviesModule.repository.MovieRepository;
import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.entity.UserWatchesMovie;
import com.MoviesRecommender.userModule.repository.UserWatchesMovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class UserControllerTest {
    /**
     * Reason why we do testing is, ki suppose tumne koi function likha, and tumhe ni pata ki voh kese kaam krega toh tum
     * springboot run kroge poori application ko usme bahut time lgta hai start krne mai, jab hume ek function test krna hai
     * toh vahi run kre na, poori application kiu run krni hai, isliye we use JUnit for unit testing jisme jis function
     * ko test krna hai sirf vahi run hoga, poori application start krne ka time waste ni hoga*/
    /**
     * We are going to do Unit Testing of UserController using JUnit
     * Unit Testing means testing individual components(functions) so we are going to test dashboard() function in UserController
     * Annotate the method with @Test
     *
     * Before testing the dashboard lets first test the func() method, Annotate it with the @Test and click the green
     * button on left of it to run just the test , spring application won't run. It will run fine
     *
     * Now dashboard() function has following dependency-:
     * user- joki session se milega, hum apna khud ka bana lenge instead
     * userWatchesMovieRepository - joki database se watched movies ki title laaegi
     * movieRepository - joki database se movies laaegi
     * movieRecommender - jo ki api call krke movies recommend krega
     * Now if you define these beans and annotate them with @Autowired, it will give Null Pointer Exception because Spring start nhi
     * hua , only test function call hua hai toh spring factory nhi bani toh beans create hi nhi hue
     * Toh ek solution toh ye hai ki is class ko @SpringBootTest annotation se mark krdo and is class ko run kro rather than running function, toh usse spring factory banegi
     * and voh inject krdegi dependencies, but voh bahit slow ho jaaega process hum nhi chahte ki poori application run
     * kro(integration testing), hum chahte hai ki hum sirf ek funtion run kre isliye hi toh junit use kra, REFER FIRST LINE
     * */

    // Since it is using objects of main package toh ye movieRepository real mai database se lekar aaegi cheeje
    // movieRecommender real mai call krega flask API ko
    // It will lead down to slower operations
    // So don't do this instead, use UserControllerTest2

    @Autowired
    private UserWatchesMovieRepository userWatchesMovieRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRecommender movieRecommender;




    @Test
    public void func(){
        Assertions.assertEquals(2,1+1);
    }

    // we can also run multiple test case together, using parameters
    // and parameters, we can get from a csv file or we can ourself give them
    @ParameterizedTest
    @CsvSource({
            "1,1,2",
            "2,3,5",
            "4,9,13"
    })
    public void func2(int a,int b,int c){
        Assertions.assertEquals(a+b,c);
    }


//    @Test
//    public void dashboard(){
//
//        // get the user
//        User user= new User("66dc6766b3db8652f55c4423","shreyyerhs07@gmail.com","12345");
//
//        // First we will fetch all movies watched by the user earlier
//        List<UserWatchesMovie> titleOfMoviesWatchedByUser = userWatchesMovieRepository.findAllByUserId(user.getId());
//
//        // Now fetch the movies, corresponding to the title
//        List<Movie> movieList=new ArrayList<>();
//        for(UserWatchesMovie userWatchesMovie:titleOfMoviesWatchedByUser){
//            Movie movie=movieRepository.findByTitle(userWatchesMovie.getMovieTitle());
//            movieList.add(movie);
//        }
//
//        // fetch the movies which are similar to the list of movies which are already watched by user
//        List<Movie> similarMovies=new ArrayList<>();
//        for (Movie movie:movieList){
//            // find the similar movies
//            List<Movie> similarMoviesToCurrentMovie=movieRecommender.recommendMovies(movie.getTitle());
//            // add it to the final list
//            similarMovies.addAll(similarMoviesToCurrentMovie);
//        }
//
//        // if the size of the list, is very big then we will remove some movies
//        if(similarMovies.size()>50){
//            int sizeOfList=similarMovies.size();
//            Random random = new Random();
//            while (sizeOfList>50){
//                int randomIndex = random.nextInt(sizeOfList);
//                similarMovies.remove(randomIndex);
//                sizeOfList--;
//            }
//        }
//
//        // if the size of the list is smaller than 50 , then show the top remaining rows from database
//        else if(similarMovies.size()<50){
//            int remainingSize=50-similarMovies.size();
//            Pageable limit = PageRequest.of(0,remainingSize);
//            Page<Movie> remainingMoviesPage = movieRepository.findAll(limit);
//            List<Movie> remainingMovies = remainingMoviesPage.stream().toList();
//            similarMovies.addAll(remainingMovies);
//        }
//
//        Assertions.assertTrue(similarMovies.size()>0);
//
//    }

}
