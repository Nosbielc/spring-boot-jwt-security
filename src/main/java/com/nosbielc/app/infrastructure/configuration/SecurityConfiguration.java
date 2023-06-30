package com.nosbielc.app.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.nosbielc.app.infrastructure.shared.constants.PathsConstants.PATH_ADMIN;
import static com.nosbielc.app.infrastructure.shared.constants.PathsConstants.PATH_MANAGEMENT;
import static com.nosbielc.app.infrastructure.shared.user.Permission.*;
import static com.nosbielc.app.infrastructure.shared.user.Role.ADMIN;
import static com.nosbielc.app.infrastructure.shared.user.Role.MANAGER;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(authorize -> {

            authorize.requestMatchers("/api/v1/auth/**",
                    "/v2/api-docs",
                    "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html").permitAll();

            authorize.requestMatchers(PATH_ADMIN+ "**").hasRole(ADMIN.name());
            authorize.requestMatchers(PATH_MANAGEMENT+ "**").hasAnyRole(ADMIN.name(), MANAGER.name());

            authorize.requestMatchers(GET, PATH_MANAGEMENT+ "**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name());
            authorize.requestMatchers(POST, PATH_MANAGEMENT+ "**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name());
            authorize.requestMatchers(PUT, PATH_MANAGEMENT+ "**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name());
            authorize.requestMatchers(DELETE, PATH_MANAGEMENT+ "**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name());


        })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(this.jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> {
                        logout.logoutSuccessUrl("/api/v1/auth/logout");
                        logout.addLogoutHandler(logoutHandler);
                        logout.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
                    })
                .build();
    }
}
