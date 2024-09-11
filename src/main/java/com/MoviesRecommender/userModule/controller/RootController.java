package com.MoviesRecommender.userModule.controller;

import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class RootController {
    Logger logger= LoggerFactory.getLogger(RootController.class);

    @Autowired
    private UserRepository userRepository;

    // this function will run everytime whenever a request is hit
    @ModelAttribute
    public void sendLoggedUserInformationToView(Model model, Authentication authentication){
        if(authentication==null){return;}

        String email="";

        // find the email of the user jisne login kra
        if(authentication instanceof OAuth2AuthenticationToken){
            System.out.println("auth2");
            OAuth2User oauth2User= (OAuth2User)authentication.getPrincipal();
            email = oauth2User.getAttribute("email").toString();
        }

        logger.info(email+"------------------------------------------------------BEFORE CONTROLLER----------------------------------------------");
        // get the user from email
        User user=userRepository.findByEmail(email);
        logger.info("user is {} ",user);
        model.addAttribute("user",user);
    }

}
