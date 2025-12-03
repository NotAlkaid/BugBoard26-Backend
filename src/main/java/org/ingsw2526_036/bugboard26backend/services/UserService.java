package org.ingsw2526_036.bugboard26backend.services;

import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean isValid(User user) {
        boolean condition = user.getEmail().isBlank() ||
                            user.getPassword().isBlank();
        return !condition;
    }
}
