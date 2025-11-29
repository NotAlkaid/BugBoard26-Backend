package org.ingsw2526_036.bugboard26backend.controllers;

import org.ingsw2526_036.bugboard26backend.dtos.UserRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.UserResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.mappers.UserMapper;
import org.ingsw2526_036.bugboard26backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
class   UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    //Metodo che ritorna una lista di tutti gli utenti nel DB
    @GetMapping("/getusers")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserResponseDto> dtousers = users
                .stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtousers);
    }

    @PostMapping("/createuser")
    public ResponseEntity<UserResponseDto> createUser(@Validated @RequestBody UserRequestDto userRequestDto) {
        User user = userMapper.toEntity(userRequestDto);
        if(!userService.isValid(user)) throw new IllegalArgumentException("Fields empty or null");
        User saveduser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(saveduser));
    }

}
