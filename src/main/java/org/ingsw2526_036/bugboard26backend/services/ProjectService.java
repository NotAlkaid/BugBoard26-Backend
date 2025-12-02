package org.ingsw2526_036.bugboard26backend.services;
import org.ingsw2526_036.bugboard26backend.dtos.ProjectRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.mappers.ProjectMapper;
import org.ingsw2526_036.bugboard26backend.repositories.AdministratorRepository;
import org.ingsw2526_036.bugboard26backend.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private AdministratorRepository administratorRepository;
    @Autowired
    private ProjectMapper projectMapper;

    //da fare controllo su nome/creator/icon non nulli o vuoti
    public Project createProject(ProjectRequestDto projectRequestDto) {
        if (projectRepository.existsByName(projectRequestDto.getName())) {
            throw new IllegalArgumentException("Project name already exists");
        }
        
        //da vede se basta per far si che solo admin creino progetti
        Administrator creator = administratorRepository.findById(projectRequestDto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("AdminId not valid"));

        Project project = projectMapper.toEntity(projectRequestDto);
        project.setCreator(creator);

        return projectRepository.save(project);
    }

}
