package org.ingsw2526_036.bugboard26backend.repositories;

import java.util.List;
import org.ingsw2526_036.bugboard26backend.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //query per trovare i commenti di una specifica issue ordinati tramite id
    List<Comment> findByIssueIdOrderByIdAsc(Long issueId);
}
