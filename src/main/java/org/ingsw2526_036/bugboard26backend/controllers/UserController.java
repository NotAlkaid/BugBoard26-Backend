package org.ingsw2526_036.bugboard26backend.controllers;

import lombok.NonNull;
import org.ingsw2526_036.bugboard26backend.dtos.UserResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.mappers.UserMapper;
import org.ingsw2526_036.bugboard26backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    //Metodo che ritorna una lista di tutti gli utenti nel DB
    @GetMapping("/getusers")
    public ResponseEntity<@NonNull List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserResponseDto> dtousers = users
                .stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtousers);
    }
}
