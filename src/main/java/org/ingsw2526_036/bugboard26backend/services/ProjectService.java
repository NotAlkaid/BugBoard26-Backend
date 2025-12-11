package org.ingsw2526_036.bugboard26backend.services;

import java.util.*;
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
    public Project createProject(ProjectRequestDto projectRequestDto, Administrator creator) {
        if (projectRepository.existsByName(projectRequestDto.getName())) {
            throw new DuplicateResourceException("Project name already exists");
        }
        Project project = projectMapper.toEntity(projectRequestDto);
        project.setCreator(creator);
        Project createdProject = projectRepository.save(project);
        addParticipant(createdProject.getId(), creator.getId(), creator);
        return projectRepository.save(project);
    }

    @Transactional
    public Project addParticipant(Long projectId, Long userId, User requester) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectId not valid"));

            
        if (!(project.getCreator().getId().equals(requester.getId()))) {
            throw new IllegalArgumentException("Only the project creator can add participants");
        }

            
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserId not valid"));
                    
            // Gestione liste (inizializzazione difensiva se null, anche se JPA di solito le istanzia)            
            if (user.getJoinedProjects() == null) user.setJoinedProjects(new ArrayList<>());
            if (project.getParticipants() == null) project.setParticipants(new ArrayList<>());

            // Aggiungo solo se non c'è già
            if (!user.getJoinedProjects().contains(project)) {
                // Modifico il lato Owning (User)
                user.getJoinedProjects().add(project);
                // Modifico anche il lato Inverse (Project) per coerenza in memoria
                project.getParticipants().add(user);
            }
            else
            {
                throw new DuplicateResourceException("User with id " + user.getId() + 
                                                     " is already a participant of the project with id " + project.getId());
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
