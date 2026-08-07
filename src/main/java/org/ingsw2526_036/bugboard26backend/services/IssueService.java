package org.ingsw2526_036.bugboard26backend.services;
import org.springframework.stereotype.Service;
import org.ingsw2526_036.bugboard26backend.repositories.IssueRepository;
import org.ingsw2526_036.bugboard26backend.repositories.ProjectRepository;
import org.ingsw2526_036.bugboard26backend.mappers.IssueMapper;
import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.dtos.IssueRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final IssueMapper issueMapper;
    private final UserRepository userRepository;

    @Transactional
    public Issue createIssue(Long projectId, IssueRequestDto issueRequestDto, User creator) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        Issue issue = issueMapper.toEntity(issueRequestDto);
        issue.setCreator(creator);
        issue.setProject(project);
        return issueRepository.save(issue);     
    }

    @Transactional
    public Issue modifyIssue(Long issueId, IssueRequestDto dto, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));

        // Verifico i permessi in base allo stato
        checkModificationPermissions(issue, requester);

        // Se passa i controlli, aggiorno i dati (ignorando lo stato)
        issueMapper.updateIssueFromDto(dto, issue);
        
        return issueRepository.save(issue);
    }

    private void checkModificationPermissions(Issue issue, User requester) {
        boolean isAdmin = requester instanceof Administrator;
        boolean isCreator = issue.getCreator().getId().equals(requester.getId());
        boolean isAssignee = issue.getAssignedTo() != null && 
                             issue.getAssignedTo().getId().equals(requester.getId());

        switch (issue.getState()) {
            case TODO:
                // In TODO: Solo Creatore o Admin
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
    public Issue promoteIssue(Long issueId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));
        
        // Regola specifica: INPROGRESS -> CLOSED
        if (issue.getState() == StateEnum.INPROGRESS) {
            boolean isAdmin = requester instanceof Administrator;
            // Controlla se è assegnata e se il richiedente è l'assegnatario
            boolean isAssignee = issue.getAssignedTo() != null && 
                                 issue.getAssignedTo().getId().equals(requester.getId());

            if (!isAssignee && !isAdmin) {
                throw new AccessDeniedException("Solo l'assegnatario o un amministratore possono chiudere la issue.");
            }
        }

        // Se passa il controllo (o se è in stato TODO, non ci sono vincoli per la promozione)
        issue.promote(); 
        
        return issueRepository.save(issue);
    }

    @Transactional
    public Issue demoteIssue(Long issueId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));
        
        boolean isAdmin = requester instanceof Administrator;
        boolean isAssignee = issue.getAssignedTo() != null && 
                             issue.getAssignedTo().getId().equals(requester.getId());

        if (issue.getState() == StateEnum.CLOSED) {
            // Riapertura di una issue chiusa: Solo Admin
            if (!isAdmin) {
                throw new AccessDeniedException("Solo un amministratore può riaprire una issue chiusa.");
            }
        } else if (issue.getState() == StateEnum.INPROGRESS) {
            // Retrocessione a TODO: Solo Assegnatario o Admin
            if (!isAssignee && !isAdmin) {
                throw new AccessDeniedException("Solo l'assegnatario o un amministratore possono retrocedere la issue a TODO.");
            }
        }
        
        issue.demote();
        
        return issueRepository.save(issue);
    }

    @Transactional
    public Issue assignIssue(Long issueId, Long userId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));
        // Solo Admin può assegnare
        if (!(requester instanceof Administrator)) {
            throw new AccessDeniedException("Only Administrators can assign issues.");
        }
        // Recupera l'utente da assegnare (puoi aggiungere UserRepository se necessario)
        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User to assign not found with id: " + userId));

        if (!assignee.getJoinedProjects().contains(issue.getProject())) {
            throw new IllegalArgumentException("User with id " + assignee.getId() + 
                                               " is not a participant of the project with id " + issue.getProject().getId());
        }

        issue.setAssignedTo(assignee);
        //aggiorno anche la lista delle issue assegnate all'utente
        if (assignee.getIssuesAssigned() == null) {
            assignee.setIssuesAssigned(new java.util.ArrayList<>());
        }
        if (!assignee.getIssuesAssigned().contains(issue)) {
            assignee.getIssuesAssigned().add(issue);
        }
        return issueRepository.save(issue);
    }
    public List<Issue> findAll() {
        return issueRepository.findAll();
    }  

}
