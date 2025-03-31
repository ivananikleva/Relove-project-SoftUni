package com.relove.service;

import com.relove.model.entity.UserEntity;
import com.relove.repo.UserRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.List;

public class ReLoveUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public ReLoveUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
          return userRepo.findByEmail(email)
                 .map(ReLoveUserDetailsService::map)
                .orElseThrow(() -> new UsernameNotFoundException("User with email" + email + "not found."));
    }

    private static UserDetails map(UserEntity userEntity) {
        return User.withUsername(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(List.of())
                .disabled(false)
                .build();
    }
}
