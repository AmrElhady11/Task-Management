package com.TaskApp.service.Impl;

import com.TaskApp.entity.Token;
import com.TaskApp.entity.User;
import com.TaskApp.enums.Role;
import com.TaskApp.enums.TokenType;
import com.TaskApp.exception.InvalidException;
import com.TaskApp.exception.NotFoundException;
import com.TaskApp.repository.TokenRepository;
import com.TaskApp.repository.UserRepository;
import com.TaskApp.request.*;
import com.TaskApp.response.AuthenticationResponse;
import com.TaskApp.service.AuthenticationService;
import com.TaskApp.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService ;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    @Override
    public void register(RegisterRequest authRequest) {

        if(userRepository.findByEmail(authRequest.getEmail()).isPresent()) {
            throw new InvalidException("Email is already in use");
        }
        var user = User.builder()
                .email(authRequest.getEmail())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .firstName(authRequest.getFirstName())
                .lastName(authRequest.getLastName())
                .role(Role.USER)
                .enabled(false)
                .build();

        userRepository.save(user);




    }

    private void revokeToken(User user) {
        var validToken = tokenRepository.findAllValidTokensByUserId(user.getId());
        if (validToken.isEmpty()) {
            return;
        }
        validToken.forEach(t->{ t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validToken);
    }
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        var user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeToken(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                ()->new NotFoundException(String.format("User with email %s not found", email)));
    }

    @Override
    public boolean enableUser(String email) {
        var user = getUser(email);
        user.setEnabled(true);
        userRepository.save(user);
        return true;

    }

    @Override
    public void deleteUser(DeleteRequest deleteRequest) {
        var user = getUser(deleteRequest.getEmail());

        if(!passwordEncoder.matches(deleteRequest.getPassword(), user.getPassword())) {
            throw new InvalidException("Wrong password!");
        }
        userRepository.delete(user);
    }
    @Override
    public void updateUser(UpdateRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if(!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidException("Wrong password!");
        }
        if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new InvalidException("Passwords do not match!");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}
