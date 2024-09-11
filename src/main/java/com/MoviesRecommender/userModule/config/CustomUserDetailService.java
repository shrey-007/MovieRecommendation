package com.MoviesRecommender.userModule.config;

import com.MoviesRecommender.userModule.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
// UserDetailsService hi user ko load krata hai, but initially jab spring security lagate hai toh defult username and password
// console pr print hota hai, hum voh default configurations nhi chahte toh new CustomUserDetailService class banai jo ki
// UserDetailsService ko implement kregi kregi, toh jab spring ko user chaiye toh voh CustomUserDetailService la loadUserByUserName call krega
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // jab bhi spring security ko user chaiye hoga toh ye function call hoga, toh yaha tum user ko jaha se bhi laana
        // chahte ho le aao
        // Hum chahte hai user ko database se laana, toh we will use UserRepository
        // since security mai humara email hi username hai, isliye findByEmail mai username pass kiya
        return userRepository.findByEmail(username);
    }


}
