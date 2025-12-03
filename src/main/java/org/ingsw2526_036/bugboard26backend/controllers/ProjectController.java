package org.ingsw2526_036.bugboard26backend.controllers;

import lombok.AllArgsConstructor;
import org.ingsw2526_036.bugboard26backend.dtos.ProjectRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.ProjectResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.mappers.ProjectMapper;
import org.ingsw2526_036.bugboard26backend.services.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @PostMapping("/createproject")
    public ResponseEntity<ProjectResponseDto> createProject(@Valid @RequestBody ProjectRequestDto projectRequestDto) {
        Project createdProject = projectService.createProject(projectRequestDto);
        ProjectResponseDto projectResponseDto = projectMapper.toDto(createdProject);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponseDto);
    }

    @GetMapping("/getprojects")
    public ResponseEntity<List<ProjectResponseDto>> getProjects() {
        List<Project> projects = projectService.findAll();
        List<ProjectResponseDto> dtoProjects = projects
                .stream()
                .map(projectMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtoProjects);
    }
}
