package org.ingsw2526_036.bugboard26backend.services;
import org.springframework.stereotype.Service;
import org.ingsw2526_036.bugboard26backend.repositories.IssueRepository;
import org.ingsw2526_036.bugboard26backend.repositories.ProjectRepository;
import org.ingsw2526_036.bugboard26backend.mappers.IssueMapper;
import lombok.RequiredArgsConstructor;
import org.ingsw2526_036.bugboard26backend.dtos.IssueRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final IssueMapper issueMapper;

    @Transactional
    public Issue createIssue(Long projectId, IssueRequestDto issueRequestDto, User creator) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        Issue issue = issueMapper.toEntity(issueRequestDto);
        issue.setCreator(creator);
        issue.setProject(project);
        return issueRepository.save(issue);     
    }

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }  

}
