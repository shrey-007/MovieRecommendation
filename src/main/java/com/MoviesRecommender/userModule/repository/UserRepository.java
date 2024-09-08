package com.MoviesRecommender.userModule.repository;

import com.MoviesRecommender.userModule.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    public User findByEmail(String email);
}
