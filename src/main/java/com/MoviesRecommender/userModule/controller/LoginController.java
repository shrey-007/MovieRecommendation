package com.MoviesRecommender.userModule.controller;

import com.MoviesRecommender.userModule.entity.User;
import com.MoviesRecommender.userModule.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    UserRepository userRepository;

    @RequestMapping("/")
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
        if(userFromDatabase.getPassword().equals(user.getPassword())){
            session.setAttribute("user",userFromDatabase);
            System.out.println(userFromDatabase+"going to dashboard--------------------------->");
            return "redirect:/dashboard";
        }
        return "login";
    }

    @RequestMapping(value = "/doRegister",method = RequestMethod.POST)
    public String register(@ModelAttribute User user){
        System.out.println(user);
        userRepository.save(user);
        return "login";
    }

}
