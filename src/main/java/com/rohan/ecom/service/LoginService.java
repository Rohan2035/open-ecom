package com.openecom.ecom.service;

import com.openecom.ecom.dto.LoginDTO;
import com.openecom.ecom.entity.RefreshToken;
import com.openecom.ecom.exceptions.InvalidCredentialsException;
import com.openecom.ecom.exceptions.TokenExpiredException;
import com.openecom.ecom.exceptions.UserDetailsNotFoundException;
import com.openecom.ecom.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class LoginService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginService(JwtUtil jwtUtil,
                        RefreshTokenRepository refreshTokenRepository,
                        AuthenticationManager authenticationManager) {

        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public String refreshToken(String refreshToken) {
        RefreshToken refreshTokenObject = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UserDetailsNotFoundException("User not found for this refresh token"));

        Instant curTime = Instant.now();
        if(curTime.isAfter(Instant.parse(refreshTokenObject.getExpiresAt()))) {
            refreshTokenRepository.delete(refreshTokenObject);
            throw new TokenExpiredException("Token expired please login again");
        }

        return jwtUtil.generateToken(refreshTokenObject.getUsername());
    }

    @Transactional
    public Map<String, String> login(LoginDTO loginDTO) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.username(),
                    loginDTO.password()
            ));

        } catch(AuthenticationException e) {
            throw new InvalidCredentialsException(e);
        }

        refreshTokenRepository.deleteByUsername(loginDTO.username());

        String refreshToken = UUID.randomUUID().toString();

        RefreshToken refreshTokenObject = new RefreshToken();
        refreshTokenObject.setUsername(loginDTO.username());
        refreshTokenObject.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS).toString());
        refreshTokenObject.setToken(refreshToken);

        refreshTokenRepository.save(refreshTokenObject);

        return Map.of(
                "refreshToken", refreshToken,
                "accessToken", jwtUtil.generateToken(loginDTO.username())
        );
    }
}
