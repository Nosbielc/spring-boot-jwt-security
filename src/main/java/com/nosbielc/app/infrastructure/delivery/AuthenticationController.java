package com.nosbielc.app.infrastructure.delivery;

import com.nosbielc.app.core.auth.AuthenticationRequest;
import com.nosbielc.app.core.auth.AuthenticationResponse;
import com.nosbielc.app.core.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface AuthenticationController {

    ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    );
    ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    );

    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;

}
