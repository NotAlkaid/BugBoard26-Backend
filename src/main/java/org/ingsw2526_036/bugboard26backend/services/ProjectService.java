package org.ingsw2526_036.bugboard26backend.services;
import org.ingsw2526_036.bugboard26backend.dtos.ProjectRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.mappers.ProjectMapper;
import org.ingsw2526_036.bugboard26backend.repositories.AdministratorRepository;
import org.ingsw2526_036.bugboard26backend.repositories.ProjectRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AdministratorRepository administratorRepository;
    private final ProjectMapper projectMapper;

    public Project createProject(ProjectRequestDto projectRequestDto) {
        if (projectRepository.existsByName(projectRequestDto.getName())) {
            throw new IllegalArgumentException("Project name already exists");
        }
    
        //solo admin esistenti possono creare progetti
        Administrator creator = administratorRepository.findById(projectRequestDto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("AdminId not valid"));

        Project project = projectMapper.toEntity(projectRequestDto);
        project.setCreator(creator);

        return projectRepository.save(project);
    }

}
