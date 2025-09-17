package com.TaskApp.service;

import com.TaskApp.request.*;
import com.TaskApp.response.AuthenticationResponse;

public interface AuthenticationService {
    void register(RegisterRequest authRequest);
    AuthenticationResponse authenticate(AuthenticationRequest authRequest);
    boolean enableUser(String email);
    void deleteUser(DeleteRequest deleteRequest);
    void updateUser(UpdateRequest updateRequest);
    void changePassword(ChangePasswordRequest changePasswordRequest);
}
