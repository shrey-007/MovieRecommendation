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
    String original_title;

    String title;

    String overview;

    // below attributes are not used, but since it is in database, so i have to include them
    Long budget;
    String genres;
    String homepage;
    String keywords;
    String original_language;
    Double popularity;
    String production_companies;
    String production_countries;

    String release_date;
    Long revenue;
    Integer runtime;
    String spoken_languages;
    String status;
    String tagline;
    Double vote_average;
    Integer vote_count;

}
