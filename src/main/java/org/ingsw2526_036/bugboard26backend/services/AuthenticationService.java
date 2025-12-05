package org.ingsw2526_036.bugboard26backend.services;


import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.config.JwtService;
import org.ingsw2526_036.bugboard26backend.dtos.AuthenticationRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.AuthenticationResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDto createUser(User user) {
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponseDto(jwtToken);
    }

    public AuthenticationResponseDto login(AuthenticationRequestDto user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        User saveduser = userRepository.findByEmail(user.getEmail()).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var jwtToken = jwtService.generateToken(saveduser);
        return new AuthenticationResponseDto(jwtToken);
    }
}
