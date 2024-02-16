package com.example.homeserviceprovider.security.token.service;

import com.example.homeserviceprovider.security.token.entity.Token;
import com.example.homeserviceprovider.security.token.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    @Override
    public void saveToken(Token token) {
        tokenRepository.save(token);
    }

    @Override
    public Optional<Token> getToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public int setConfirmedAt(String token) {
        return tokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
