package org.ingsw2526_036.bugboard26backend.services;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.config.JwtService;
import org.ingsw2526_036.bugboard26backend.dtos.AuthenticationRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.AuthenticationResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.exception.DuplicateResourceException;
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

    @Transactional
    public AuthenticationResponseDto createUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already in use");
        }
        if(userRepository.existsByUsername(user.getRealUsername())) {
            throw new DuplicateResourceException("Username already in use");
        }
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
