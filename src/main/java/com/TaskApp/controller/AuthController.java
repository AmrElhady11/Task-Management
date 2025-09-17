package com.TaskApp.controller;


import com.TaskApp.request.AuthenticationRequest;
import com.TaskApp.request.RegisterRequest;
import com.TaskApp.response.AuthenticationResponse;
import com.TaskApp.service.AuthenticationService;
import com.TaskApp.service.OtpService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final OtpService otpService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest authRequest)
    {
        authenticationService.register(authRequest);
        String email = authRequest.getEmail();
        otpService.sendOTPCode(email);
        return ResponseEntity.ok(String.format("Successfully registered and OTP code has sent to %s", email));

    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authRequest){
        return ResponseEntity.ok(authenticationService.authenticate(authRequest));


    }
    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String email,@RequestParam String code){
            otpService.checkOtpCode(email,code);
            authenticationService.enableUser(email);
            return ResponseEntity.ok("Activated");

    }
    @GetMapping("/regenerateOTP")
    public ResponseEntity<String> regenerateOTP(@RequestParam String email){
        otpService.deleteAllOtpCodes(email);
        otpService.sendOTPCode(email);
        return ResponseEntity.ok("OTP Code Generated and has been reset");
    }



}

