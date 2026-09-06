package org.ingsw2526_036.bugboard26backend.services;

import org.springframework.stereotype.Service;
import org.ingsw2526_036.bugboard26backend.repositories.IssueRepository;
import org.ingsw2526_036.bugboard26backend.repositories.ProjectRepository;
import org.ingsw2526_036.bugboard26backend.mappers.IssueMapper;
import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.dtos.IssueFilterDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueResponseDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueSummaryDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Label;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import org.ingsw2526_036.bugboard26backend.exception.ResourceNotFoundException;
import org.ingsw2526_036.bugboard26backend.repositories.LabelRepository;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;
import org.ingsw2526_036.bugboard26backend.specifications.IssueSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IssueService {

    private static final String ISSUE_NOT_FOUND_MSG = "Issue not found with id: ";

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final IssueMapper issueMapper;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    @Transactional
    public Issue createIssue(Long projectId, IssueRequestDto issueRequestDto, User creator) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        Issue issue = issueMapper.toEntity(issueRequestDto);
        issue.setCreator(creator);
        issue.setProject(project);

        if (issueRequestDto.getLabelIds() != null && !issueRequestDto.getLabelIds().isEmpty()) {
            List<Label> labels = labelRepository.findAllById(issueRequestDto.getLabelIds());
            issue.setLabels(new HashSet<>(labels));
        }

        return issueRepository.save(issue);
    }

    @Transactional
    public IssueResponseDto updateIssue(Long issueId, IssueRequestDto dto, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(ISSUE_NOT_FOUND_MSG + issueId));

        // Verifico i permessi in base allo stato
        checkModificationPermissions(issue, requester);

        // Se passa i controlli, aggiorno i dati (ignorando lo stato)
        issueMapper.updateIssueFromDto(dto, issue);

        if (dto.getLabelIds() != null) {
            List<Label> labels = labelRepository.findAllById(dto.getLabelIds());
            issue.setLabels(new HashSet<>(labels));
        }

        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.toDto(savedIssue);
    }

    public void checkModificationPermissions(Issue issue, User requester) {
        boolean isAdmin = requester instanceof Administrator;
        boolean isCreator = issue.getCreator().getId().equals(requester.getId());
        boolean isAssignee = issue.getAssignedTo() != null &&
                issue.getAssignedTo().getId().equals(requester.getId());

        switch (issue.getState()) {
            case TODO:
                // Nello stato iniziale (da fare): Solo Creatore o Admin
                if (!isCreator && !isAdmin) {
                    throw new AccessDeniedException("Solo il creatore o un amministratore possono modificare una issue in stato TODO.");
                }
                break;
            case INPROGRESS:
                // In INPROGRESS: Solo Assegnatario o Admin
                if (!isAssignee && !isAdmin) {
                    throw new AccessDeniedException("Solo l'assegnatario o un amministratore possono modificare una issue in stato INPROGRESS.");
                }
                break;
            case CLOSED:
                // Solo Admin
                if (!isAdmin) {
                    throw new AccessDeniedException("Solo un amministratore può modificare una issue chiusa.");
                }
                break;
        }
    }

    @Transactional
    public IssueResponseDto promoteIssue(Long issueId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(ISSUE_NOT_FOUND_MSG + issueId));

        // Il passaggio da TO-DO a INPROGRESS deve avvenire esclusivamente tramite assegnazione (assignIssue)
        if (issue.getState() == StateEnum.TODO) {
            throw new IllegalStateException("An issue in TODO state cannot be promoted directly; it must be assigned by an administrator.");
        }

        // Regola specifica: INPROGRESS -> CLOSED
        if (issue.getState() == StateEnum.INPROGRESS) {
            // Non è possibile chiudere una issue se non è assegnata
            if (issue.getAssignedTo() == null) {
                throw new IllegalStateException("An issue in INPROGRESS state must have an assignee before it can be closed.");
            }

            boolean isAdmin = requester instanceof Administrator;
            boolean isAssignee = issue.getAssignedTo().getId().equals(requester.getId());

            if (!isAssignee && !isAdmin) {
                throw new AccessDeniedException("Solo l'assegnatario o un amministratore possono chiudere la issue.");
            }
        }

        issue.promote();

        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.toDto(savedIssue);
    }

    @Transactional
    public IssueResponseDto demoteIssue(Long issueId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(ISSUE_NOT_FOUND_MSG + issueId));

        boolean isAdmin = requester instanceof Administrator;

        if (issue.getState() == StateEnum.CLOSED && !isAdmin) {
            // Riapertura di una issue chiusa: Solo Admin
            throw new AccessDeniedException("Solo un amministratore può riaprire una issue chiusa.");
        } else if (issue.getState() == StateEnum.INPROGRESS) {
            // Retrocessione allo stato iniziale: Solo Admin
            if (!isAdmin) {
                throw new AccessDeniedException("Solo un amministratore può demote la issue a TODO.");
            }
            // Rimozione dell'assegnatario quando si torna in TO-DO
            if (issue.getAssignedTo() != null) {
                User oldAssignee = issue.getAssignedTo();
                if (oldAssignee.getIssuesAssigned() != null) {
                    oldAssignee.getIssuesAssigned().remove(issue);
                }
                issue.setAssignedTo(null);
            }
        }

        issue.demote();

        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.toDto(savedIssue);
    }

    @Transactional
    public IssueResponseDto assignIssue(Long issueId, Long userId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(ISSUE_NOT_FOUND_MSG + issueId));
        // Solo Admin può assegnare
        if (!(requester instanceof Administrator)) {
            throw new AccessDeniedException("Only Administrators can assign issues.");
        }
        // Recupera l'utente da assegnare
        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User to assign not found with id: " + userId));

        if (assignee.getJoinedProjects() == null || !assignee.getJoinedProjects().contains(issue.getProject())) {
            throw new IllegalArgumentException("User with id " + assignee.getId() +
                    " is not a participant of the project with id " + issue.getProject().getId());
        }

        // Se era precedentemente assegnata a qualcun altro, rimuovi la issue dalle assegnazioni del vecchio utente
        if (issue.getAssignedTo() != null && !issue.getAssignedTo().getId().equals(assignee.getId())) {
            User oldAssignee = issue.getAssignedTo();
            if (oldAssignee.getIssuesAssigned() != null) {
                oldAssignee.getIssuesAssigned().remove(issue);
            }
        }

        issue.setAssignedTo(assignee);
        // Aggiorno anche la lista delle issue assegnate all'utente
        if (assignee.getIssuesAssigned() == null) {
            assignee.setIssuesAssigned(new java.util.ArrayList<>());
        }
        if (!assignee.getIssuesAssigned().contains(issue)) {
            assignee.getIssuesAssigned().add(issue);
        }

        // Se la issue è in stato TO-DO, l'assegnazione la porta automaticamente in INPROGRESS
        if (issue.getState() == StateEnum.TODO) {
            issue.promote();
        }

        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.toDto(savedIssue);
    }

    @Transactional
    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    @Transactional
    public Page<IssueResponseDto> getIssues(Long projectId,
                                            IssueFilterDto filter,
                                            Pageable pageable) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }

        Specification<Issue> spec = IssueSpecification.withFilters(projectId, filter);

        return issueRepository.findAll(spec, pageable)
                .map(issueMapper::toDto);
    }

    @Transactional
    public IssueResponseDto getIssueById(Long projectId, Long issueId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(ISSUE_NOT_FOUND_MSG + issueId));

        if (!issue.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException("Issue with id " + issueId + " does not belong to project " + projectId);
        }

        return issueMapper.toDto(issue);
    }

    @Transactional
    public IssueSummaryDto getIssueSummary(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        long total = issueRepository.countByProjectId(projectId);
        long open = issueRepository.countByProjectIdAndStateIn(projectId, List.of(StateEnum.TODO, StateEnum.INPROGRESS));
        long bugs = issueRepository.countByProjectIdAndType(projectId, TypeEnum.BUG);
        long closed = issueRepository.countByProjectIdAndState(projectId, StateEnum.CLOSED);
        return new IssueSummaryDto(total, open, bugs, closed);
    }

    @Transactional
    public IssueResponseDto addLabelToIssue(Long issueId, Long labelId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(ISSUE_NOT_FOUND_MSG + issueId));
        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + labelId));

        checkModificationPermissions(issue, requester);

        if (issue.getLabels().size() >= 10 && !issue.getLabels().contains(label)) {
            throw new IllegalArgumentException("An issue cannot have more than 10 labels.");
        }

        issue.getLabels().add(label);
        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.toDto(savedIssue);
    }

    @Transactional
    public IssueResponseDto removeLabelFromIssue(Long issueId, Long labelId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(ISSUE_NOT_FOUND_MSG + issueId));
        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + labelId));

        checkModificationPermissions(issue, requester);

        issue.getLabels().remove(label);
        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.toDto(savedIssue);
    }

    @Transactional
    public IssueResponseDto setIssueLabels(Long issueId, Set<Long> labelIds, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(ISSUE_NOT_FOUND_MSG + issueId));

        checkModificationPermissions(issue, requester);

        if (labelIds != null && labelIds.size() > 10) {
            throw new IllegalArgumentException("An issue cannot have more than 10 labels.");
        }

        Set<Label> newLabels = new HashSet<>();
        if (labelIds != null && !labelIds.isEmpty()) {
            newLabels.addAll(labelRepository.findAllById(labelIds));
        }
        issue.setLabels(newLabels);
        Issue savedIssue = issueRepository.save(issue);
        return issueMapper.toDto(savedIssue);
    }
}
