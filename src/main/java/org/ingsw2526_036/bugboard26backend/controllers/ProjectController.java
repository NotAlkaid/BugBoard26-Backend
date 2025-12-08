package org.ingsw2526_036.bugboard26backend.controllers;

import java.util.List;

import org.ingsw2526_036.bugboard26backend.dtos.ProjectRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.ProjectResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.mappers.ProjectMapper;
import org.ingsw2526_036.bugboard26backend.services.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ingsw2526_036.bugboard26backend.dtos.UserResponseDto;
import lombok.NonNull;
import org.ingsw2526_036.bugboard26backend.mappers.UserMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/projects")
@AllArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    @PostMapping("/createproject")
    public ResponseEntity<ProjectResponseDto> createProject(@Valid @RequestBody ProjectRequestDto projectRequestDto,
                                                            @AuthenticationPrincipal User creator) {
        Project createdProject = projectService.createProject(projectRequestDto, creator);
        ProjectResponseDto projectResponseDto = projectMapper.toDto(createdProject);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponseDto);
    }

    //Endpoint: GET /api/projects/getprojects
    @GetMapping("/getprojects")
    public ResponseEntity<List<ProjectResponseDto>> getProjects() {
        List<Project> projects = projectService.findAll();
        List<ProjectResponseDto> dtoProjects = projects
                .stream()
                .map(projectMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtoProjects);
    }
    
    /**
     * Add multiple users to a project.
     *
     * Purpose: Allow the administrator who created the project to add multiple users as participants.
     * Pattern: retrieves the authenticated requester from the security context
     *          and delegates authorization and business logic to the service layer.
     *
     * Endpoint: POST /api/projects/{projectId}/participants
     * Body: JSON array of user ids, e.g. [1,2,3]
     */
    @PostMapping("/{projectId}/participants")
    public ResponseEntity<ProjectResponseDto> addParticipants(@PathVariable Long projectId,
                                                              @NotEmpty(message = "UserIds list cannot be empty") @RequestBody List<Long> userIds,
                                                              @AuthenticationPrincipal User requester) {
        Project updatedProject = projectService.addParticipants(projectId, userIds, requester);
        ProjectResponseDto projectResponseDto = projectMapper.toDto(updatedProject);
        return ResponseEntity.ok(projectResponseDto);
    }

    @GetMapping("/{projectId}/participants")
    public ResponseEntity<@NonNull List<UserResponseDto>> getProjectParticipants(@PathVariable Long projectId) {
        Project project = projectService.findById(projectId);
        List<UserResponseDto> participantDtos = project.getParticipants()
                .stream()
                .map(userMapper::toDto)
                .toList();
        return ResponseEntity.ok(participantDtos);
    }

}
