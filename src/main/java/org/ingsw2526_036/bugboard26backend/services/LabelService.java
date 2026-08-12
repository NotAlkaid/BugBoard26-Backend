package org.ingsw2526_036.bugboard26backend.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ingsw2526_036.bugboard26backend.dtos.LabelRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Label;
import org.ingsw2526_036.bugboard26backend.entities.User;
import org.ingsw2526_036.bugboard26backend.exception.ResourceNotFoundException;
import org.ingsw2526_036.bugboard26backend.repositories.IssueRepository;
import org.ingsw2526_036.bugboard26backend.repositories.LabelRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final IssueRepository issueRepository;
    private final IssueService issueService;

    @Transactional
    public Label createLabel(LabelRequestDto dto) {
        if (labelRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Label with name '" + dto.getName() + "' already exists");
        }
        Label label = new Label();
        label.setName(dto.getName());
        label.setColor(dto.getColor() != null && !dto.getColor().isBlank() ? dto.getColor() : "#6C757D");
        return labelRepository.save(label);
    }

    public List<Label> getAllLabels() {
        return labelRepository.findAll();
    }

    public Label getLabelById(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));
    }

    @Transactional
    public Label updateLabel(Long id, LabelRequestDto dto) {
        Label label = getLabelById(id);
        if (dto.getName() != null && !dto.getName().equals(label.getName())) {
            if (labelRepository.existsByName(dto.getName())) {
                throw new IllegalArgumentException("Label with name '" + dto.getName() + "' already exists");
            }
            label.setName(dto.getName());
        }
        if (dto.getColor() != null && !dto.getColor().isBlank()) {
            label.setColor(dto.getColor());
        }
        return labelRepository.save(label);
    }

    @Transactional
    public void deleteLabel(Long id) {
        Label label = getLabelById(id);
        for (Issue issue : label.getIssues()) {
            issue.getLabels().remove(label);
            issueRepository.save(issue);
        }
        labelRepository.delete(label);
    }

    @Transactional
    public Issue addLabelToIssue(Long issueId, Long labelId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));
        Label label = getLabelById(labelId);

        issueService.checkModificationPermissions(issue, requester);

        issue.getLabels().add(label);
        return issueRepository.save(issue);
    }

    @Transactional
    public Issue removeLabelFromIssue(Long issueId, Long labelId, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));
        Label label = getLabelById(labelId);

        issueService.checkModificationPermissions(issue, requester);

        issue.getLabels().remove(label);
        return issueRepository.save(issue);
    }

    @Transactional
    public Issue setIssueLabels(Long issueId, Set<Long> labelIds, User requester) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));

        issueService.checkModificationPermissions(issue, requester);

        Set<Label> newLabels = new HashSet<>();
        if (labelIds != null && !labelIds.isEmpty()) {
            newLabels.addAll(labelRepository.findAllById(labelIds));
        }
        issue.setLabels(newLabels);
        return issueRepository.save(issue);
    }
}
