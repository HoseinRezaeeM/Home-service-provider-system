package com.example.homeserviceprovider.security.token.service;

import com.example.homeserviceprovider.security.token.entity.Token;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface TokenService {

    void saveToken(Token token);

    Optional<Token> getToken(String token);

    int setConfirmedAt(String token);

}