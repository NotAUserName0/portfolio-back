package com.portfolio.porfolio.utils.Components;

import com.portfolio.porfolio.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import com.portfolio.porfolio.config.PasswordParserConfig;
import com.portfolio.porfolio.models.User;

@Component
public class UserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordParserConfig passwordParserConfig;

    @Value("${admin.default.username}")
    private String defaultUsername;

    @Value("${admin.default.password}")
    private String defaultPassword;

    public UserInitializer(PasswordParserConfig passwordParserConfig, UserRepository userRepository) {
        this.passwordParserConfig = passwordParserConfig;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername(defaultUsername) == null) {
            User newUser = new User();
            newUser.setUsername(defaultUsername);
            newUser.setPassword(passwordParserConfig.passwordEncoder().encode(defaultPassword));
            userRepository.save(newUser);
            System.out.println("Admin user created with username: " + defaultUsername);
        } else {
            System.out.println("Admin user already exists with username: " + defaultUsername);
        }
    }
}
