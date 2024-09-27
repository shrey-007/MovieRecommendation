package com.MoviesRecommender.moviesModule.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(title, movie.title); // Compare based on title, since title is unique, I have already done data preprocessing
    }

    @Override
    public int hashCode() {
        return Objects.hash(title); // Ensure the hashCode is based on title, since title is unique, I have already done data preprocessing
    }
}
