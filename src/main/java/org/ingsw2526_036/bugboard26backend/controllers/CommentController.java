package org.ingsw2526_036.bugboard26backend.controllers;

import java.util.List;

import org.ingsw2526_036.bugboard26backend.dtos.CommentRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.CommentResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Comment;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.mappers.CommentMapper;
import org.ingsw2526_036.bugboard26backend.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@RestController
@RequestMapping("/api/projects/{projectId}/issues/{issueId}/comments")
@AllArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping({"", "/addcomment"})
    public ResponseEntity<@NonNull CommentResponseDto> addComment(@PathVariable Long projectId,
                                                                 @PathVariable Long issueId,
                                                                 @Valid @RequestBody CommentRequestDto commentRequestDto,
                                                                 @AuthenticationPrincipal User creator) {
        Comment comment = commentService.addComment(projectId, issueId, commentRequestDto, creator);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toDto(comment));
    }

    @GetMapping({"", "/getcomments"})
    public ResponseEntity<@NonNull List<CommentResponseDto>> getCommentsByIssue(@PathVariable Long projectId,
                                                                               @PathVariable Long issueId) {
        List<Comment> comments = commentService.getCommentsByIssue(projectId, issueId);
        List<CommentResponseDto> dtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}