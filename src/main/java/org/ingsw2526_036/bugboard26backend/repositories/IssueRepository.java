package org.ingsw2526_036.bugboard26backend.repositories;

import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;

public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    long countByProjectId(Long projectId);
    long countByProjectIdAndStateIn(Long projectId, Collection<StateEnum> states);
    long countByProjectIdAndType(Long projectId, TypeEnum type);
    long countByProjectIdAndState(Long projectId, StateEnum state);
}
