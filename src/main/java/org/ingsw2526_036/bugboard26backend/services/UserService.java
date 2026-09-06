package org.ingsw2526_036.bugboard26backend.services;

import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findUsers(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            // rimuovo spazi all'inizio e alla fine
            String clean = search.trim();
            return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(clean, clean, pageable);
        }
        return userRepository.findAll(pageable);
    }
}
