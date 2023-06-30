package com.nosbielc.app.core.auth.ports;

import com.nosbielc.app.core.auth.AuthenticationRequest;
import com.nosbielc.app.core.auth.AuthenticationResponse;
import com.nosbielc.app.core.auth.RegisterRequest;
import com.nosbielc.app.infrastructure.persistence.entities.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);

    void saveUserToken(UserEntity user, String jwtToken);

    void revokeAllUserTokens(UserEntity user);

    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;
}
