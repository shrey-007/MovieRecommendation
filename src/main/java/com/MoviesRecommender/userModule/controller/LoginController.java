package com.MoviesRecommender.userModule.controller;

import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/register")
    public String register(){
        return "register";
    }

    @RequestMapping(value = "/doLogin",method = RequestMethod.POST)
    public String doLogin(@ModelAttribute User user, HttpSession session,Model model){
        User userFromDatabase = userRepository.findByEmail(user.getEmail());
        log.info("User logged in the application: {}", userFromDatabase);
        if(userFromDatabase.getPassword().equals(user.getPassword())){
            session.setAttribute("user",userFromDatabase);
            return "redirect:/dashboard";
        }
        return "login";
    }

    @RequestMapping(value = "/doRegister",method = RequestMethod.POST)
    public String register(@ModelAttribute User user){
        log.info("User registered in the application: {}", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

}
