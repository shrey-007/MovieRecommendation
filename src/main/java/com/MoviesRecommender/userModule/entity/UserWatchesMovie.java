package com.MoviesRecommender.userModule.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userWatchesMovie")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserWatchesMovie {
    @Id
    String id;
    String userId;
    String movieTitle;

    public UserWatchesMovie(String userId, String movieTitle) {
        this.userId = userId;
        this.movieTitle = movieTitle;
    }
}
