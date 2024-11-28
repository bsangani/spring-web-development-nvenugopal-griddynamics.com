package com.gd.ecom.controller;

import com.gd.ecom.exception.InvalidLoginException;
import com.gd.ecom.exception.UserAlreadyExistsException;
import com.gd.ecom.model.UserDto;
import com.gd.ecom.records.LoginRequest;
import com.gd.ecom.records.LoginSuccessResponse;
import com.gd.ecom.service.impl.AccountServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Base64;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    @Autowired
    private final AccountServiceImpl accountService;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
        if (accountService.addUser(userDto)) {
            return ResponseEntity.ok().build();
        } else {
            throw new UserAlreadyExistsException("User already exists with email "+userDto.getEmail(), HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Authenticate user and create a session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = @Content(schema = @Schema(implementation = LoginSuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest, HttpSession session) {
        if (accountService.authenticate(loginRequest.email(), loginRequest.password())) {
            var encodedSessionId = Base64.getEncoder().encodeToString(session.getId().getBytes());
            var successResponse = new LoginSuccessResponse(true, encodedSessionId);   //By Default, it is encoded by Base 64 and set to the cookie by spring session.(In task it is mentioned so sending as Json Response).
            return ResponseEntity.ok(successResponse);
        } else {
            throw new InvalidLoginException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }
}
