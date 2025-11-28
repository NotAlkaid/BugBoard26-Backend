package org.ingsw2526_036.bugboard26backend.repositories;

import org.ingsw2526_036.bugboard26backend.entities.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

}
