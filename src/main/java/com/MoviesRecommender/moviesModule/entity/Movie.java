package com.MoviesRecommender.moviesModule.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("movies")
@Getter
@ToString
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    Object id;
    String title;

    String overview;
}
