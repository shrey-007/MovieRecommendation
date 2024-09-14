package com.MoviesRecommender.userModule.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class LoggedUserManager {

    public static String getEmailOfLoggedUser(Authentication authentication){

        if(authentication instanceof OAuth2AuthenticationToken){
            OAuth2User oauth2User= (OAuth2User)authentication.getPrincipal();
            return oauth2User.getAttribute("email").toString();
        }
        else{
            return authentication.getName();
        }
    }
}
