package com.MoviesRecommender.moviesModule.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import com.MoviesRecommender.moviesModule.entity.Movie;
import com.MoviesRecommender.moviesModule.helper.MovieRecommender;
import com.MoviesRecommender.moviesModule.repository.MovieRepository;
import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.entity.UserWatchesMovie;
import com.MoviesRecommender.userModule.repository.UserWatchesMovieRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest2 {
    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserWatchesMovieRepository userWatchesMovieRepository;

    @Mock
    private MovieRecommender movieRecommender;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    private User mockUser;
    private List<UserWatchesMovie> mockWatchedMovies;
    private List<Movie> mockMovieList;

    @BeforeEach
    public void setup() {
        // Setup mock data
        mockUser = new User("mockId","mockUser","mockPassword");

        // Setup mock watched movies
        mockWatchedMovies = new ArrayList<>();
        mockWatchedMovies.add(new UserWatchesMovie("mockId", "Movie 1"));
        mockWatchedMovies.add(new UserWatchesMovie("mockId", "Movie 2"));

        // Setup mock movies
        mockMovieList = new ArrayList<>();
        Movie movie1 = new Movie();
        Movie movie2 = new Movie();
        movie1.setTitle("Movie 1");
        movie2.setTitle("Movie 2");
        mockMovieList.add(movie1);
        mockMovieList.add(movie2);
    }

    @Test
    public void testDashboard() {
        // Mock session behavior
        when(model.getAttribute("user")).thenReturn(mockUser);

        // Mock repository behavior
        when(userWatchesMovieRepository.findAllByUserId(mockUser.getId())).thenReturn(mockWatchedMovies);
        Movie sampleMovie = new Movie();
        sampleMovie.setTitle("Sample Movie");
        when(movieRepository.findByTitle(anyString())).thenReturn(sampleMovie);

        // Mock movie recommender behavior
        when(movieRecommender.recommendMovies(anyString())).thenReturn(mockMovieList);

        // Mock repository behavior for fetching remaining movies
        Page<Movie> mockPage = new PageImpl<>(mockMovieList);
        when(movieRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);

        // Call the dashboard method
        String viewName = userController.dashboard(model);

        // Verify interactions and assertions
        verify(userWatchesMovieRepository, times(1)).findAllByUserId(mockUser.getId());
        verify(movieRepository, times(2)).findByTitle(anyString());
        verify(movieRecommender, times(2)).recommendMovies(anyString());
        verify(model, times(1)).addAttribute(eq("moviesList"), anyList());

        assertEquals("dashboard", viewName);
    }
}

