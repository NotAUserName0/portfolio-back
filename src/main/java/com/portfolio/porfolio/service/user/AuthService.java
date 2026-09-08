package com.portfolio.porfolio.service.user;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.portfolio.porfolio.models.User;
import com.portfolio.porfolio.repository.UserRepository;
import com.portfolio.porfolio.service.JwtService;
import com.portfolio.porfolio.config.PasswordParserConfig;
import com.portfolio.porfolio.dto.AuthDto;
import com.portfolio.porfolio.dto.UserDto;
import com.portfolio.porfolio.utils.ApiResponse;
import com.portfolio.porfolio.utils.HttpStatuses;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordParserConfig passwordParser;
    private final JwtService jwtService;

    public AuthService(
        UserRepository userRepository,
        PasswordParserConfig passwordParser,
        JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordParser = passwordParser;
        this.jwtService = jwtService;
    }

    /**
     * @description Authenticates user credentials and generates a signed JWT token.
     *              Mitigates User Enumeration attacks by returning an identical 401 UNAUTHORIZED response
     *              and generic error message regardless of whether the user exists or the password is wrong.
     * @param user The login payload containing username and password.
     * @return ApiResponse containing the JWT token upon success, or a generic 401 error upon failure.
     */
    public ApiResponse<Object> loginService(UserDto user) {
        User foundUser = userRepository.findByUsername(user.getUsername());

        // Defense against User Enumeration: return identical generic message if user is not found
        if (foundUser == null) {
            return ApiResponse.error(HttpStatuses.UNAUTHORIZED, UserMessages.INVALID_CREDENTIALS);
        }

        Boolean validPassword = passwordParser.passwordEncoder().matches(user.getPassword(), foundUser.getPassword());

        // Defense against User Enumeration: return identical generic message if password mismatch
        if (!validPassword) {
            return ApiResponse.error(HttpStatuses.UNAUTHORIZED, UserMessages.INVALID_CREDENTIALS);
        }

        String token = jwtService.generateToken(new HashMap<>(), foundUser);
        AuthDto authDto = new AuthDto(token);

        return ApiResponse.success(UserMessages.LOGIN_SUCCESS, authDto);
    }
}
