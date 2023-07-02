package com.nosbielc.app.infrastructure.delivery.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosbielc.app.core.auth.AuthenticationRequest;
import com.nosbielc.app.core.auth.AuthenticationResponse;
import com.nosbielc.app.core.auth.RegisterRequest;
import com.nosbielc.app.infrastructure.persistence.repositories.TokenRepository;
import com.nosbielc.app.infrastructure.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nosbielc.app.infrastructure.shared.user.Role.ADMIN;
import static com.nosbielc.app.infrastructure.shared.user.Role.MANAGER;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerImplTest {

    private static final String URL_BASE = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private List<RegisterRequest> registerRequests = new ArrayList<>();
    private List<AuthenticationRequest> authenticationRequests = new ArrayList<>();
    private AuthenticationResponse accessToken = AuthenticationResponse.builder().build();

    @BeforeEach
    void setUp() {

        for (int i = 0; i < 10; i++) {

            var email = i + "_fourtest@gmail.com";
            var password = i + "_pass_fake";

            registerRequests.add(RegisterRequest.builder()
                    .email(email)
                    .firstname(i + "_four")
                    .lastname(i + "_test")
                    .password(password)
                    .role(i % 2 == 0 ? MANAGER : ADMIN)
                    .build());

            authenticationRequests.add(AuthenticationRequest.builder()
                            .email(email)
                            .password(password)
                    .build());
        }

    }

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"})
    void testRegister(String index) throws Exception {
        var registerRequest = getFirstRegisterRequest(index);

        if (registerRequest.isPresent()) {
            mockMvc.perform(MockMvcRequestBuilders.post(URL_BASE.concat("/register"))
                            .content(getJson(registerRequest.get()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token").exists())
                    .andExpect(jsonPath("$.refresh_token").exists());
        } else {
            throw new Exception();
        }
    }

    @Test
    void testAuthenticate() throws Exception {
        var authenticationRequest = AuthenticationRequest.builder()
                .email("admin@mail.com")
                .password("password")
                .build();

        var result =  mockMvc.perform(MockMvcRequestBuilders.post(URL_BASE.concat("/authenticate"))
                         .content(getJson(authenticationRequest))
                         .contentType(MediaType.APPLICATION_JSON)
                         .accept(MediaType.APPLICATION_JSON))
                 .andDo(print())
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.access_token").exists())
                 .andExpect(jsonPath("$.refresh_token").exists())
                .andReturn();

        accessToken = getObject(result.getResponse().getContentAsString());
    }
    @Test
    void testRefreshToken()  throws Exception {

        var user = userRepository.findByEmail("admin@mail.com").get();
        var tokens = tokenRepository.findAllValidTokenByUser(user.getId());

        mockMvc.perform(MockMvcRequestBuilders.post(URL_BASE.concat("/refresh-token"))
                        .header("Authorization", "Bearer ".concat(tokens.get(0).getToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andReturn();
    }

    private Optional<AuthenticationRequest> getFirstAuthenticationRequest(String initialIndex) {
        return authenticationRequests.stream()
                .filter(registerRequest1 -> registerRequest1.getEmail().startsWith(initialIndex.concat("_")))
                .findFirst();
    }

    private Optional<RegisterRequest> getFirstRegisterRequest(String initialIndex) {
        return registerRequests.stream()
                .filter(registerRequest1 -> registerRequest1.getEmail().startsWith(initialIndex.concat("_")))
                .findFirst();
    }

    private String getJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private AuthenticationResponse getObject(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, AuthenticationResponse.class);
    }
}
