package org.ingsw2526_036.bugboard26backend.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.ingsw2526_036.bugboard26backend.dtos.ProjectRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.exception.DuplicateResourceException;
import org.ingsw2526_036.bugboard26backend.exception.ResourceNotFoundException;
import org.ingsw2526_036.bugboard26backend.mappers.ProjectMapper;
import org.ingsw2526_036.bugboard26backend.repositories.ProjectRepository;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service layer for project-related operations.
 * Purpose: Encapsulates business logic for creating projects and managing project participants.
 * This class delegates persistence to repositories and uses mappers for DTO conversions.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    @Transactional
    public Project createProject(ProjectRequestDto projectRequestDto, User creator) {
        if (projectRepository.existsByName(projectRequestDto.getName())) {
            throw new DuplicateResourceException("Project name already exists");
        }
        if (!(creator instanceof Administrator)) {
            throw new IllegalArgumentException("Only administrators can create projects");
        }
        Project project = projectMapper.toEntity(projectRequestDto);
        project.setCreator((Administrator) creator);

        return projectRepository.save(project);
    }

    /**
     * Add multiple users as participants to a project.
     *
     * Purpose: Given a project id and a list of user ids, attach those users as participants
     *          to the project if they exist and are not already members. Only the administrator
     *          who created the project is allowed to perform this operation.
     * Arguments:
     *  - projectId: id of the project to update.
     *  - userIds: list of user ids to add as participants.
     *  - requester: the authenticated user performing the action (must be the project's creator).
     * Return:
     *  - The updated and persisted Project entity.
     * Exceptions:
     *  - ResourceNotFoundException when project or any user id is invalid.
     *  - IllegalArgumentException when requester is not authorized.
     */
    @Transactional
    public Project addParticipants(Long projectId, List<Long> userIds, User requester) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectId not valid"));

        
        if (!(project.getCreator().getId().equals(requester.getId()))) {
            throw new IllegalArgumentException("Only the project creator can add participants");
        }

        Set<Long> distinctUserIds = new HashSet<>(userIds);

        List<User> usersToAdd = userRepository.findAllById(distinctUserIds);

        if (usersToAdd.size() != distinctUserIds.size()) {
            // Creo un Set con gli ID che ho trovato nel DB
            Set<Long> foundIds = usersToAdd.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            
            // Filtro la lista originale per trovare quelli che NON sono nel Set
            List<Long> missingIds = distinctUserIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            
            // Lancio l'eccezione elencando esattamente gli ID mancanti
            throw new ResourceNotFoundException("The following UserIds are not valid: " + missingIds);
        }

        for (User user : usersToAdd) {
            // Gestione liste (inizializzazione difensiva se null, anche se JPA di solito le istanzia)
            if (user.getJoinedProjects() == null) user.setJoinedProjects(new ArrayList<>());
            if (project.getParticipants() == null) project.setParticipants(new ArrayList<>());

            // Aggiungo solo se non c'è già
            if (!user.getJoinedProjects().contains(project)) {
                // FONDAMENTALE: Modifico il lato Owning (User)
                user.getJoinedProjects().add(project);
                // Modifico anche il lato Inverse (Project) per coerenza in memoria
                project.getParticipants().add(user);
            }
        }

        return projectRepository.save(project);
    }
 
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectId not valid"));
    }
}
