package org.ingsw2526_036.bugboard26backend.controllers;


import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.dtos.AuthenticationRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.AuthenticationResponseDto;
import org.ingsw2526_036.bugboard26backend.dtos.UserRequestDto;
import org.ingsw2526_036.bugboard26backend.mappers.UserMapper;
import org.ingsw2526_036.bugboard26backend.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping("/createuser")
    public ResponseEntity<@NonNull AuthenticationResponseDto> createUser(@Valid @RequestBody UserRequestDto request) {
        var user = userMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(authenticationService.createUser(user));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<@NonNull AuthenticationResponseDto> authenticate(@Valid @RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.login(request));
    }

}
