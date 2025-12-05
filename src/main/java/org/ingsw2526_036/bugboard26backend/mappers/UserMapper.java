package org.ingsw2526_036.bugboard26backend.mappers;

import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.dtos.UserRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.UserResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.entities.BaseUser;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    //Prende un utente e restituisce un dto
    public UserResponseDto toDto(User user) {
        String type = "BASEUSER";
        if (user == null) return null;
        if(user instanceof Administrator) type = "ADMIN";
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                type
        );
    }

    //Prende un dto e restituisce un utente
    public User toEntity(UserRequestDto userRequestDto) {
        User user;
        if("ADMIN".equalsIgnoreCase(userRequestDto.getType())){
            user = new Administrator();
            user.setEmail(userRequestDto.getEmail());
            user.setUsername(userRequestDto.getUsername());
            user.setPassword(Objects.requireNonNull(passwordEncoder.encode(userRequestDto.getPassword())));
            return user;
        }
        else if("BASEUSER".equalsIgnoreCase(userRequestDto.getType())){
            user = new BaseUser();
            user.setEmail(userRequestDto.getEmail());
            user.setUsername(userRequestDto.getUsername());
            user.setPassword(Objects.requireNonNull(passwordEncoder.encode(userRequestDto.getPassword())));
            return user;
        }
        throw new IllegalArgumentException("Unknown user type");
    }
}
