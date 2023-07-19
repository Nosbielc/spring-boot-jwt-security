package com.nosbielc.app.infrastructure.delivery.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosbielc.app.core.auth.AuthenticationRequest;
import com.nosbielc.app.core.auth.AuthenticationResponse;
import com.nosbielc.app.core.auth.RegisterRequest;
import com.nosbielc.app.infrastructure.persistence.repositories.TokenRepository;
import com.nosbielc.app.infrastructure.persistence.repositories.UserRepository;
import com.nosbielc.app.infrastructure.shared.user.Role;
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

import java.util.*;
import java.util.function.Predicate;

import static com.nosbielc.app.infrastructure.shared.user.Role.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerImplTest {

    private static final String URL_BASE = "/api/v1/admin";
    private static final String URL_AUTH = "/api/v1/auth";
    private static final String PASSWORD = "Password2023";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    private HashMap<RegisterRequest, AuthenticationResponse> registerRequestList = new HashMap<>();

    @BeforeEach
    void setUp() throws Exception {
        tokenRepository.deleteAll();
        userRepository.deleteAll();

        var userBasic = RegisterRequest.builder()
                .email("user@gmail.com")
                .firstname("user")
                .lastname("commun")
                .password(PASSWORD)
                .role(USER)
                .build();
        var userBasicAutenticate = registrerUser(userBasic);
        registerRequestList.put(userBasic, userBasicAutenticate);


        var userManager = RegisterRequest.builder()
                .email("manager@gmail.com")
                .firstname("manager")
                .lastname("man")
                .password(PASSWORD)
                .role(MANAGER)
                .build();
        var userManagerAutenticate = registrerUser(userManager);
        registerRequestList.put(userManager, userManagerAutenticate);

        var userAdmin = RegisterRequest.builder()
                .email("admin@gmail.com")
                .firstname("admin")
                .lastname("adm")
                .password(PASSWORD)
                .role(ADMIN)
                .build();
        var userAdminAutenticate = registrerUser(userAdmin);

        registerRequestList.put(userAdmin, userAdminAutenticate);

    }

    @ParameterizedTest
    @CsvSource({"USER", "MANAGER", "ADMIN"})
    void testGet(String role) throws Exception {
        var roleTest = Role.valueOf(role);
        var authenticate = getAutenticationResponse(roleTest);

        if (roleTest.name().equalsIgnoreCase("ADMIN")) {
            mockMvc.perform(MockMvcRequestBuilders.get(URL_BASE)
                            .header("Authorization", "Bearer ".concat(authenticate.getAccessToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("GET:: admin controller"))
                    .andReturn();
        } else {
            mockMvc.perform(MockMvcRequestBuilders.get(URL_BASE)
                            .header("Authorization", "Bearer ".concat(authenticate.getAccessToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        }

    }

    @ParameterizedTest
    @CsvSource({"USER", "MANAGER", "ADMIN"})
    void testPost(String role) throws Exception {
        var roleTest = Role.valueOf(role);
        var authenticate = getAutenticationResponse(roleTest);

        if (roleTest.name().equalsIgnoreCase("ADMIN")) {
            mockMvc.perform(MockMvcRequestBuilders.post(URL_BASE)
                            .header("Authorization", "Bearer ".concat(authenticate.getAccessToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("POST:: admin controller"))
                    .andReturn();
        } else {
            mockMvc.perform(MockMvcRequestBuilders.post(URL_BASE)
                            .header("Authorization", "Bearer ".concat(authenticate.getAccessToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        }
    }

    @ParameterizedTest
    @CsvSource({"USER", "MANAGER", "ADMIN"})
    void testPut(String role) throws Exception {
        var roleTest = Role.valueOf(role);
        var authenticate = getAutenticationResponse(roleTest);

        if (roleTest.name().equalsIgnoreCase("ADMIN")) {
            mockMvc.perform(MockMvcRequestBuilders.put(URL_BASE)
                            .header("Authorization", "Bearer ".concat(authenticate.getAccessToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("PUT:: admin controller"))
                    .andReturn();
        } else {
            mockMvc.perform(MockMvcRequestBuilders.put(URL_BASE)
                            .header("Authorization", "Bearer ".concat(authenticate.getAccessToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        }
    }

    @ParameterizedTest
    @CsvSource({"USER", "MANAGER", "ADMIN"})
    void testDelete(String role) throws Exception {
        var roleTest = Role.valueOf(role);
        var authenticate = getAutenticationResponse(roleTest);

        if (roleTest.name().equalsIgnoreCase("ADMIN")) {
            mockMvc.perform(MockMvcRequestBuilders.delete(URL_BASE)
                            .header("Authorization", "Bearer ".concat(authenticate.getAccessToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string("DELETE:: admin controller"))
                    .andReturn();
        } else {
            mockMvc.perform(MockMvcRequestBuilders.delete(URL_BASE)
                            .header("Authorization", "Bearer ".concat(authenticate.getAccessToken()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn();
        }
    }

    private AuthenticationResponse getAutenticationResponse(Role role) throws Exception {
        Predicate<Object> filterPredicate = key -> {
            if (key != null) {
                Role _role= ((RegisterRequest) key).getRole();
                return _role.equals(role);
            }
            return false;
        };

        Map<RegisterRequest, AuthenticationResponse> filteredMap = new HashMap<>();
        for (Map.Entry<RegisterRequest, AuthenticationResponse> entry : registerRequestList.entrySet()) {
            RegisterRequest key = entry.getKey();
            if (filterPredicate.test(key)) {
                filteredMap.put(key, entry.getValue());
                return entry.getValue();
            }
        }

        throw new Exception("Fatal Error");
    }

    AuthenticationResponse registrerUser(RegisterRequest user) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post(URL_AUTH.concat("/register"))
                        .content(getJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andReturn();
        return getObject(result.getResponse().getContentAsString());
    }

    private String getJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private AuthenticationResponse getObject(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, AuthenticationResponse.class);
    }
}