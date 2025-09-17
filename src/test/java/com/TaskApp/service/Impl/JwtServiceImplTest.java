package com.TaskApp.service.Impl;

import com.TaskApp.entity.User;
import com.TaskApp.enums.Role;
import com.TaskApp.repository.TokenRepository;
import com.TaskApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtServiceImpl unit tests")
class JwtServiceImplTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1)
                .email("test@example.com")
                .password("encodedPass")
                .firstName("amr")
                .lastName("elhady")
                .role(Role.USER)
                .enabled(false)
                .build();
    }

    @Nested
    @DisplayName("Generate and validate token")
    class TokenGenerationAndValidation {
        @Test
        @DisplayName("should generate valid token and validate it successfully")
        void generateAndValidateToken() {
            String token = jwtService.generateToken(mockUser);

            assertNotNull(token);
            assertTrue(jwtService.isTokenValid(token, mockUser));
        }

        @Test
        @DisplayName("should extract username correctly from token")
        void extractUsernameFromToken() {
            String token = jwtService.generateToken(mockUser);

            String username = jwtService.extractUserName(token);

            assertEquals("test@example.com", username);
        }
    }

    @Nested
    @DisplayName("Token expiration")
    class TokenExpiration {
        @Test
        @DisplayName("should return false when token expired")
        void expiredToken() throws InterruptedException {
            String token = jwtService.generateToken(mockUser);


            assertTrue(jwtService.isTokenValid(token, mockUser));
        }
    }
}
