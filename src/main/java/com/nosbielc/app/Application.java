package com.nosbielc.app;

import com.nosbielc.app.core.auth.RegisterRequest;
import com.nosbielc.app.core.auth.ports.AuthenticationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.nosbielc.app.infrastructure.shared.user.Role.ADMIN;
import static com.nosbielc.app.infrastructure.shared.user.Role.MANAGER;

@Log4j2
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service
    ) {
        return args -> {
            var admin = RegisterRequest.builder()
                    .firstname("Admin")
                    .lastname("Administrator")
                    .email("admin@mail.com")
                    .password("password")
                    .role(ADMIN)
                    .build();
            log.info("Admin token: " + service.register(admin).getAccessToken());

            var manager = RegisterRequest.builder()
                    .firstname("Manager")
                    .lastname("Administrator")
                    .email("manager@mail.com")
                    .password("password")
                    .role(MANAGER)
                    .build();
            log.info("Manager token: " + service.register(manager).getAccessToken());

        };
    }
}
