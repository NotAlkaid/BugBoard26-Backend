package org.ingsw2526_036.bugboard26backend.repositories;

import java.util.Optional;
import org.ingsw2526_036.bugboard26backend.entities.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findByName(String name);
    boolean existsByName(String name);
}
