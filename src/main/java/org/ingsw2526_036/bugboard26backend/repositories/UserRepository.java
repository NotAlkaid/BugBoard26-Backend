package org.ingsw2526_036.bugboard26backend.repositories;

import org.ingsw2526_036.bugboard26backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
