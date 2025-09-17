package com.TaskApp.service.Impl;

import com.TaskApp.entity.Otp;
import com.TaskApp.entity.User;
import com.TaskApp.enums.Role;
import com.TaskApp.exception.ExpirationException;
import com.TaskApp.exception.InvalidException;
import com.TaskApp.exception.NotFoundException;
import com.TaskApp.repository.OtpRepository;
import com.TaskApp.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OtpServiceImpl unit test")
class OtpServiceImplTest {

    @Mock
    private JavaMailSender emailSender;
    @Mock
    private OtpRepository otpRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private OtpServiceImpl otpServiceImpl;

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
    @DisplayName("sendOTPCode tests")
    class SendOtpCodeTest {
        @Test
        @DisplayName("should send OTP and save to repository when user exists")
        void shouldSendOtpCode() {
            when(userRepository.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(mockUser));

            otpServiceImpl.sendOTPCode("test@example.com");

            verify(otpRepository).save(any(Otp.class));
            verify(emailSender).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("should throw NotFoundException when user does not exist")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findByEmail("wrong@example.com"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> otpServiceImpl.sendOTPCode("wrong@example.com"));
        }
    }

    @Nested
    @DisplayName("deleteAllOtpCodes tests")
    class DeleteOtpTest {
        @Test
        @DisplayName("should delete OTPs by userId")
        void shouldDeleteOtps() {
            when(userRepository.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(mockUser));

            otpServiceImpl.deleteAllOtpCodes("test@example.com");

            verify(otpRepository).deleteOtpByUserID(mockUser.getId());
        }
    }

    @Nested
    @DisplayName("checkOtpCode tests")
    class CheckOtpTest {
        @Test
        @DisplayName("should pass when OTP is valid")
        void shouldPassWhenOtpValid() {
            Otp otp = Otp.builder()
                    .otp("123456")
                    .expirationTime(LocalDateTime.now().plusMinutes(10))
                    .user(mockUser)
                    .build();

            when(userRepository.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(mockUser));
            when(otpRepository.findValidOtpByUserID(mockUser.getId()))
                    .thenReturn(otp);

            assertDoesNotThrow(() -> otpServiceImpl.checkOtpCode("test@example.com", "123456"));
        }

        @Test
        @DisplayName("should throw NotFoundException when no OTP found")
        void shouldThrowWhenOtpNotFound() {
            when(userRepository.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(mockUser));
            when(otpRepository.findValidOtpByUserID(mockUser.getId()))
                    .thenReturn(null);

            assertThrows(NotFoundException.class,
                    () -> otpServiceImpl.checkOtpCode("test@example.com", "123456"));
        }

        @Test
        @DisplayName("should throw ExpirationException when OTP expired")
        void shouldThrowWhenOtpExpired() {
            Otp otp = Otp.builder()
                    .otp("123456")
                    .expirationTime(LocalDateTime.now().minusMinutes(1))
                    .user(mockUser)
                    .build();

            when(userRepository.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(mockUser));
            when(otpRepository.findValidOtpByUserID(mockUser.getId()))
                    .thenReturn(otp);

            assertThrows(ExpirationException.class,
                    () -> otpServiceImpl.checkOtpCode("test@example.com", "123456"));
        }

        @Test
        @DisplayName("should throw InvalidException when OTP does not match")
        void shouldThrowWhenOtpInvalid() {
            Otp otp = Otp.builder()
                    .otp("654321")
                    .expirationTime(LocalDateTime.now().plusMinutes(10))
                    .user(mockUser)
                    .build();

            when(userRepository.findByEmail("test@example.com"))
                    .thenReturn(Optional.of(mockUser));
            when(otpRepository.findValidOtpByUserID(mockUser.getId()))
                    .thenReturn(otp);

            assertThrows(InvalidException.class,
                    () -> otpServiceImpl.checkOtpCode("test@example.com", "123456"));
        }
    }
}
