package com.MoviesRecommender.moviesModule.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("userDashboard")
@Getter
@ToString
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboard {
    @Id
    private String userId;

    private List<Movie> movies;
}
