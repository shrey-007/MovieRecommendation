package com.MoviesRecommender.userModule.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Document(collection = "user")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    String id;
    @Indexed(unique = true)
    String email;
    String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
