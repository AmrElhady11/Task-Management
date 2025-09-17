package com.TaskApp.service.Impl;

import com.TaskApp.entity.Token;
import com.TaskApp.entity.User;
import com.TaskApp.enums.Role;
import com.TaskApp.exception.InvalidException;
import com.TaskApp.exception.NotFoundException;
import com.TaskApp.repository.TokenRepository;
import com.TaskApp.repository.UserRepository;
import com.TaskApp.request.*;
import com.TaskApp.response.AuthenticationResponse;
import com.TaskApp.service.JwtService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationServiceImpl unit test")
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationServiceImpl;

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
    @DisplayName("register tests")
    class RegisterTests {
        @Test
        @DisplayName("should register new user when email not exists")
        void shouldRegisterUser() {
            RegisterRequest req = new RegisterRequest("amr", "elhady", "test@example.com", "12345");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

            authenticationServiceImpl.register(req);

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw InvalidException when email already exists")
        void shouldThrowWhenEmailExists() {
            RegisterRequest req = new RegisterRequest("amr", "elhady", "test@example.com", "12345");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

            assertThrows(InvalidException.class, () -> authenticationServiceImpl.register(req));
        }
    }

    @Nested
    @DisplayName("authenticate tests")
    class AuthenticateTests {
        @Test
        @DisplayName("should authenticate and return token")
        void shouldAuthenticate() {
            AuthenticationRequest req = new AuthenticationRequest("test@example.com", "12345");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
            when(jwtService.generateToken(mockUser)).thenReturn("jwt-token");

            AuthenticationResponse response = authenticationServiceImpl.authenticate(req);

            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenRepository).save(any(Token.class));
        }
    }

    @Nested
    @DisplayName("enableUser tests")
    class EnableUserTests {
        @Test
        @DisplayName("should enable user successfully")
        void shouldEnableUser() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

            boolean result = authenticationServiceImpl.enableUser("test@example.com");

            assertTrue(result);
            assertTrue(mockUser.isEnabled());
            verify(userRepository).save(mockUser);
        }

        @Test
        @DisplayName("should throw NotFoundException when user not found")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> authenticationServiceImpl.enableUser("wrong@example.com"));
        }
    }

    @Nested
    @DisplayName("deleteUser tests")
    class DeleteUserTests {
        @Test
        @DisplayName("should delete user when password matches")
        void shouldDeleteUser() {
            DeleteRequest req = new DeleteRequest("test@example.com", "12345");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches("12345", mockUser.getPassword())).thenReturn(true);

            authenticationServiceImpl.deleteUser(req);

            verify(userRepository).delete(mockUser);
        }

        @Test
        @DisplayName("should throw InvalidException when password mismatch")
        void shouldThrowWhenPasswordWrong() {
            DeleteRequest req = new DeleteRequest("test@example.com", "wrong");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches("wrong", mockUser.getPassword())).thenReturn(false);

            assertThrows(InvalidException.class, () -> authenticationServiceImpl.deleteUser(req));
        }
    }

    @Nested
    @DisplayName("updateUser tests")
    class UpdateUserTests {
        @Test
        @DisplayName("should update user info successfully")
        void shouldUpdateUser() {
            UpdateRequest req = new UpdateRequest("newamr", "newelhady", "new@example.com");

            Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            authenticationServiceImpl.updateUser(req);

            assertEquals("newamr", mockUser.getFirstName());
            assertEquals("newelhady", mockUser.getLastName());
            assertEquals("new@example.com", mockUser.getEmail());
            verify(userRepository).save(mockUser);
        }
    }

    @Nested
    @DisplayName("changePassword tests")
    class ChangePasswordTests {
        @Test
        @DisplayName("should change password when oldPassword matches and confirm matches new")
        void shouldChangePassword() {
            ChangePasswordRequest req = new ChangePasswordRequest("12345", "newPass", "newPass");

            Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            when(passwordEncoder.matches("12345", mockUser.getPassword())).thenReturn(true);
            when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

            authenticationServiceImpl.changePassword(req);

            assertEquals("encodedNewPass", mockUser.getPassword());
            verify(userRepository).save(mockUser);
        }

        @Test
        @DisplayName("should throw InvalidException when oldPassword mismatch")
        void shouldThrowWhenOldPasswordWrong() {
            ChangePasswordRequest req = new ChangePasswordRequest("wrong", "newPass", "newPass");

            Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            when(passwordEncoder.matches("wrong", mockUser.getPassword())).thenReturn(false);

            assertThrows(InvalidException.class, () -> authenticationServiceImpl.changePassword(req));
        }

        @Test
        @DisplayName("should throw InvalidException when newPassword != confirmPassword")
        void shouldThrowWhenPasswordsMismatch() {
            ChangePasswordRequest req = new ChangePasswordRequest("12345", "newPass", "different");

            Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            when(passwordEncoder.matches("12345", mockUser.getPassword())).thenReturn(true);

            assertThrows(InvalidException.class, () -> authenticationServiceImpl.changePassword(req));
        }
    }
}
