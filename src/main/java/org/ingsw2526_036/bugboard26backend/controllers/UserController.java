package org.ingsw2526_036.bugboard26backend.controllers;

import lombok.NonNull;
import org.ingsw2526_036.bugboard26backend.dtos.UserResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.mappers.UserMapper;
import org.ingsw2526_036.bugboard26backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.ingsw2526_036.bugboard26backend.dtos.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    @GetMapping
    public ResponseEntity<@NonNull PageResponseDto<UserResponseDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String property = ("username".equalsIgnoreCase(sortBy) || "email".equalsIgnoreCase(sortBy)) ? sortBy : "id";
        int safePage = Math.max(0, page);
        int safeSize = Math.clamp(size, 1, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, property));

        Page<UserResponseDto> dtoPage = userService.findUsers(search, pageable)
                .map(userMapper::toDto);

        return ResponseEntity.ok(PageResponseDto.from(dtoPage));
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
