package org.ingsw2526_036.bugboard26backend.controllers;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;
import org.ingsw2526_036.bugboard26backend.services.IssueService;
import org.ingsw2526_036.bugboard26backend.mappers.IssueMapper;

import java.util.List;

import org.ingsw2526_036.bugboard26backend.dtos.IssueRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueResponseDto;
import org.ingsw2526_036.bugboard26backend.dtos.ProjectResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;



@RestController
@RequestMapping("/api/projects/{projectId}/issues")
@AllArgsConstructor
@Validated
public class IssueController {

    private final IssueService issueService;
    private final IssueMapper issueMapper;

    @PostMapping("/createissue")
    public ResponseEntity<@NonNull IssueResponseDto> createIssue(@PathVariable Long projectId,
                                                                 @Valid @RequestBody IssueRequestDto issueRequestDto,
                                                                 @AuthenticationPrincipal User creator) {                                                            
        Issue createdIssue = issueService.createIssue(projectId, issueRequestDto, creator);
        return ResponseEntity.status(HttpStatus.CREATED).body(issueMapper.toDto(createdIssue));

    }


    @GetMapping("/getissues")
    public ResponseEntity<@NonNull List<IssueResponseDto>> getIssues() {
        List<Issue> issues = issueService.findAll();
            List<IssueResponseDto> dtoIssues = issues
                    .stream()
                    .map(issueMapper::toDto)
                    .toList();
            return ResponseEntity.ok(dtoIssues);
        }
}

