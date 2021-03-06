package com.ffmoyano.jwtcrud.service;

import com.ffmoyano.jwtcrud.entity.AppUser;
import com.ffmoyano.jwtcrud.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // this overrided method defines how the authentication is realized by the authentication manager
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email);
        if ( user == null) {
            throw new UsernameNotFoundException("User not found in the database");
        }
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        user.getRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getName())));
        return new User(user.getEmail(), user.getPassword(), authorities);
    }

    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
