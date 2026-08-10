package org.ingsw2526_036.bugboard26backend.services;

import java.util.List;

import org.ingsw2526_036.bugboard26backend.dtos.CommentRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.entities.Comment;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.exception.ResourceNotFoundException;
import org.ingsw2526_036.bugboard26backend.mappers.CommentMapper;
import org.ingsw2526_036.bugboard26backend.repositories.CommentRepository;
import org.ingsw2526_036.bugboard26backend.repositories.IssueRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public Comment addComment(Long projectId, Long issueId, CommentRequestDto dto, User creator) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));

        if (!issue.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Issue with id " + issueId + " does not belong to project with id " + projectId);
        }

        boolean isAdmin = creator instanceof Administrator;
        boolean isParticipant = creator.getJoinedProjects() != null &&
                creator.getJoinedProjects().stream().anyMatch(p -> p.getId().equals(projectId));

        if (!isAdmin && !isParticipant) {
            throw new AccessDeniedException("User is not authorized to add comments to this project.");
        }

        Comment comment = commentMapper.toEntity(dto);
        comment.setCreator(creator);
        comment.setIssue(issue);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByIssue(Long projectId, Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));

        if (!issue.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Issue with id " + issueId + " does not belong to project with id " + projectId);
        }

        return commentRepository.findByIssueIdOrderByIdAsc(issueId);
    }
}
