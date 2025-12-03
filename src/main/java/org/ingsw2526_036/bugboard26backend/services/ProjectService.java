package org.ingsw2526_036.bugboard26backend.services;
import java.util.ArrayList;
import java.util.List;

import org.ingsw2526_036.bugboard26backend.dtos.ProjectRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.mappers.ProjectMapper;
import org.ingsw2526_036.bugboard26backend.repositories.AdministratorRepository;
import org.ingsw2526_036.bugboard26backend.repositories.ProjectRepository;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service layer for project-related operations.
 *
 * Purpose: Encapsulates business logic for creating projects and managing project participants.
 * This class delegates persistence to repositories and uses mappers for DTO conversions.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public Project createProject(ProjectRequestDto projectRequestDto) {
        if (projectRepository.existsByName(projectRequestDto.getName())) {
            throw new IllegalArgumentException("Project name already exists");
        }
    
        //solo admin esistenti possono creare progetti
        User creator = userRepository.findById(projectRequestDto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("UserId not valid"));
        if (!(creator instanceof Administrator)) {
            throw new IllegalArgumentException("Only administrators can create projects");
        }
        Project project = projectMapper.toEntity(projectRequestDto);
        project.setCreator((Administrator) creator);

        return projectRepository.save(project);
    }

}
