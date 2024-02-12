package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.domain.user.Users;
import com.example.homeserviceprovider.repository.base.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> users = usersRepository.findByEmail(username);
        if (users.isEmpty())
            throw new UsernameNotFoundException("not found!");
        return users.get();
    }
}
