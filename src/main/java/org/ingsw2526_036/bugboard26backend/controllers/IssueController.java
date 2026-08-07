package org.ingsw2526_036.bugboard26backend.controllers;
import java.util.List;

import org.ingsw2526_036.bugboard26backend.dtos.IssueRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.mappers.IssueMapper;
import org.ingsw2526_036.bugboard26backend.services.IssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;



@RestController
@RequestMapping("/api/projects/{projectId}/issues")
@AllArgsConstructor
@Validated
public class IssueController {

    private final IssueService issueService;
    private final IssueMapper issueMapper;

    //Endpoint: POST /api/projects/{projectId}/issues/createissue
    @PostMapping("/createissue")
    public ResponseEntity<@NonNull IssueResponseDto> createIssue(@PathVariable Long projectId,
                                                                 @Valid @RequestBody IssueRequestDto issueRequestDto,
                                                                 @AuthenticationPrincipal User creator) {                                                            
        Issue createdIssue = issueService.createIssue(projectId, issueRequestDto, creator);
        return ResponseEntity.status(HttpStatus.CREATED).body(issueMapper.toDto(createdIssue));

    }
    //Endpoint: GET /api/projects/{projectId}/issues/getissues.
    @GetMapping("/getissues")
    public ResponseEntity<@NonNull List<IssueResponseDto>> getIssues() {
        List<Issue> issues = issueService.findAll();
            List<IssueResponseDto> dtoIssues = issues
                    .stream()
                    .map(issueMapper::toDto)
                    .toList();
            return ResponseEntity.ok(dtoIssues);
    }

    //Endpoint PUT /api/projects/{projectId}/issues/{issueId}.
    @PutMapping("/{issueId}") 
    public ResponseEntity<IssueResponseDto> updateIssue(@PathVariable Long issueId,
                                                        @Valid @RequestBody IssueRequestDto dto,
                                                        @AuthenticationPrincipal User requester) {
        // Passiamo il requester al service per i controlli
        Issue updatedIssue = issueService.modifyIssue(issueId, dto, requester);
        return ResponseEntity.ok(issueMapper.toDto(updatedIssue));
    }

    //Endpoint PATCH /api/projects/{projectId}/issues/{issueId}/promote.
    @PatchMapping("/{issueId}/promote")
    public ResponseEntity<IssueResponseDto> promoteIssue(@PathVariable Long issueId,
                                                         @AuthenticationPrincipal User requester) {
        // Passiamo il requester
        Issue updatedIssue = issueService.promoteIssue(issueId, requester);
        return ResponseEntity.ok(issueMapper.toDto(updatedIssue));
    }

    //Endpoint PATCH /api/projects/{projectId}/issues/{issueId}/demote.
    @PatchMapping("/{issueId}/demote")
    public ResponseEntity<IssueResponseDto> demoteIssue(@PathVariable Long issueId,
                                                        @AuthenticationPrincipal User requester) {
        // Passiamo il requester
        Issue updatedIssue = issueService.demoteIssue(issueId, requester);
        return ResponseEntity.ok(issueMapper.toDto(updatedIssue));
    }

    @PatchMapping("/{issueId}/assign/{userId}")
    public ResponseEntity<IssueResponseDto> assignIssue(@PathVariable Long issueId,
                                                        @PathVariable Long userId,
                                                        @AuthenticationPrincipal User requester) {
        Issue updatedIssue = issueService.assignIssue(issueId, userId, requester);
        return ResponseEntity.ok(issueMapper.toDto(updatedIssue)); 
    }

}

