package com.MoviesRecommender.userModule.repository;

import com.MoviesRecommender.userModule.entity.UserWatchesMovie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserWatchesMovieRepository extends MongoRepository<UserWatchesMovie,String> {
    public List<UserWatchesMovie> findAllByUserId(String userId);

}
