package com.MoviesRecommender.userModule.config;

import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    // this method will run when google se successful login ho jaaye
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("Success Handler is called for the user");
        // get the information of the user
        DefaultOAuth2User user=(DefaultOAuth2User)authentication.getPrincipal();

        String email=user.getAttribute("email").toString();
        String name=user.getAttribute("name").toString();

        User user1=new User();
        user1.setEmail(email);
        log.info("Success Handler is called for the user {} ",user1);
        // password store krne ki need nhi hai

//        user1.setEnabled(true);
//        user1.setEmailVerified(true);


        // if the user email is already in database, means ya toh ye login vala case hai ya fir user ne normal registration kr tha
        if (userRepository.findByEmail(email)!=null){
            log.info("This is login case, as user is already registered");
            response.sendRedirect("/dashboard");
        }
        else{
            // means ki ye signup ka case hai
            log.info("Saving the user in database");
            // save the user
            userRepository.save(user1);
            response.sendRedirect("/dashboard");
        }
        log.info("onAuthenticationSuccess");
    }


}
