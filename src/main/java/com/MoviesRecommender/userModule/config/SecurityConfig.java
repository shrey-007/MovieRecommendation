package com.MoviesRecommender.userModule.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@Slf4j
public class SecurityConfig {

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    private OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authenticationProvider(authenticationProvider())  // Set authentication provider
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/user/**").authenticated();
                    authorize.anyRequest().permitAll();
                })
                .formLogin(formLogin -> {
                    formLogin.loginPage("/login")
                            .loginProcessingUrl("/authenticate")
                            .defaultSuccessUrl("/dashboard", true)  // Redirect to /dashboard on success
                            .usernameParameter("email")
                            .passwordParameter("password");
                })
                .oauth2Login(oauth -> {
                    oauth.loginPage("/login")
                            .successHandler(oAuthAuthenticationSuccessHandler);
                });

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

