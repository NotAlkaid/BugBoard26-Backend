package org.ingsw2526_036.bugboard26backend.controllers;

import java.util.Set;

import org.ingsw2526_036.bugboard26backend.dtos.IssueFilterDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueResponseDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueSummaryDto;
import org.ingsw2526_036.bugboard26backend.dtos.PageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import org.ingsw2526_036.bugboard26backend.mappers.IssueMapper;
import org.ingsw2526_036.bugboard26backend.services.IssueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@RestController
@RequestMapping("/api/projects/{projectId}/issues")
@AllArgsConstructor
@Validated
public class IssueController {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "id", "title", "description", "creationDate", "priority", "state", "type"
    );

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

    // Endpoint: GET /api/projects/{projectId}/issues e /getissues
    @GetMapping({"", "/getissues"})
    public ResponseEntity<@NonNull PageResponseDto<IssueResponseDto>> getIssues(
            @PathVariable Long projectId,
            @RequestParam(required = false) TypeEnum type,
            @RequestParam(required = false) StateEnum state,
            @RequestParam(required = false) PriorityEnum priority,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) Long labelId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String property = (sortBy != null && ALLOWED_SORT_PROPERTIES.contains(sortBy)) ? sortBy : "creationDate";
        int safePage = Math.max(0, page);
        int safeSize = Math.clamp(size, 1, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, property));

        IssueFilterDto filter = new IssueFilterDto(type, state, priority, assignedToId, labelId, search);
        Page<IssueResponseDto> dtoPage = issueService.getIssues(projectId, filter, pageable);
        return ResponseEntity.ok(PageResponseDto.from(dtoPage));
    }

    // Endpoint: GET /api/projects/{projectId}/issues/summary
    @GetMapping("/summary")
    public ResponseEntity<@NonNull IssueSummaryDto> getIssueSummary(@PathVariable Long projectId) {
        IssueSummaryDto summary = issueService.getIssueSummary(projectId);
        return ResponseEntity.ok(summary);
    }

    //Endpoint PUT /api/projects/{projectId}/issues/{issueId}.
    @PutMapping("/{issueId}")
    public ResponseEntity<IssueResponseDto> updateIssue(@PathVariable Long issueId,
                                                        @Valid @RequestBody IssueRequestDto dto,
                                                        @AuthenticationPrincipal User requester) {
        IssueResponseDto responseDto = issueService.updateIssue(issueId, dto, requester);
        return ResponseEntity.ok(responseDto);
    }

    //Endpoint PATCH /api/projects/{projectId}/issues/{issueId}/promote.
    @PatchMapping("/{issueId}/promote")
    public ResponseEntity<IssueResponseDto> promoteIssue(@PathVariable Long issueId,
                                                         @AuthenticationPrincipal User requester) {
        IssueResponseDto responseDto = issueService.promoteIssue(issueId, requester);
        return ResponseEntity.ok(responseDto);
    }

    //Endpoint PATCH /api/projects/{projectId}/issues/{issueId}/demote.
    @PatchMapping("/{issueId}/demote")
    public ResponseEntity<IssueResponseDto> demoteIssue(@PathVariable Long issueId,
                                                        @AuthenticationPrincipal User requester) {
        IssueResponseDto responseDto = issueService.demoteIssue(issueId, requester);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{issueId}/assign/{userId}")
    public ResponseEntity<IssueResponseDto> assignIssue(@PathVariable Long issueId,
                                                        @PathVariable Long userId,
                                                        @AuthenticationPrincipal User requester) {
        IssueResponseDto responseDto = issueService.assignIssue(issueId, userId, requester);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{issueId}/labels/{labelId}")
    public ResponseEntity<IssueResponseDto> addLabelToIssue(@PathVariable Long issueId,
                                                            @PathVariable Long labelId,
                                                            @AuthenticationPrincipal User requester) {
        IssueResponseDto responseDto = issueService.addLabelToIssue(issueId, labelId, requester);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{issueId}/labels/{labelId}")
    public ResponseEntity<IssueResponseDto> removeLabelFromIssue(@PathVariable Long issueId,
                                                                 @PathVariable Long labelId,
                                                                 @AuthenticationPrincipal User requester) {
        IssueResponseDto responseDto = issueService.removeLabelFromIssue(issueId, labelId, requester);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{issueId}/labels")
    public ResponseEntity<IssueResponseDto> setIssueLabels(@PathVariable Long issueId,
                                                           @RequestBody Set<Long> labelIds,
                                                           @AuthenticationPrincipal User requester) {
        IssueResponseDto responseDto = issueService.setIssueLabels(issueId, labelIds, requester);
        return ResponseEntity.ok(responseDto);
    }
}