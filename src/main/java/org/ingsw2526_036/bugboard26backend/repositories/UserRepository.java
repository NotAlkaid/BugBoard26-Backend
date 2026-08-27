package org.ingsw2526_036.bugboard26backend.repositories;

import lombok.NonNull;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
