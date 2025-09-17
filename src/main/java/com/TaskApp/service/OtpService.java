package com.TaskApp.service;

public interface OtpService {
    void sendOTPCode(String email);
    void deleteAllOtpCodes(String email);
    void checkOtpCode(String email, String code);
}
