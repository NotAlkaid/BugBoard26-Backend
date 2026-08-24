package org.ingsw2526_036.bugboard26backend.services;

import org.ingsw2526_036.bugboard26backend.dtos.IssueFilterDto;
import org.ingsw2526_036.bugboard26backend.entities.BaseUser;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import org.ingsw2526_036.bugboard26backend.exception.ResourceNotFoundException;
import org.ingsw2526_036.bugboard26backend.mappers.IssueMapper;
import org.ingsw2526_036.bugboard26backend.repositories.IssueRepository;
import org.ingsw2526_036.bugboard26backend.repositories.LabelRepository;
import org.ingsw2526_036.bugboard26backend.repositories.ProjectRepository;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private IssueMapper issueMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LabelRepository labelRepository;

    @InjectMocks
    private IssueService issueService;

    private Project sampleProject;
    private BaseUser creatorUser;

    @BeforeEach
    void setUp() {
        creatorUser = new BaseUser();
        creatorUser.setId(1L);

        sampleProject = new Project();
        sampleProject.setId(1L);
    }


    @Nested
    @DisplayName("Test per il metodo getIssues")
    class GetIssuesTests {

        @Test
        @DisplayName("getIssues lancia ResourceNotFoundException se il progetto non esiste")
        void getIssues_projectNotFound_throwsResourceNotFoundException() {
            Long projectId = 999L;
            when(projectRepository.existsById(projectId)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> issueService.getIssues(projectId, null, null, null)
            );

            assertEquals("Project not found with id: " + projectId, exception.getMessage());
            verify(projectRepository, times(1)).existsById(projectId);
            verifyNoInteractions(issueRepository);
        }

        @Test
        @DisplayName("getIssues usa ordinamento di default (DESC, creationDate) con parametri null")
        void getIssues_validProject_defaultSortingAndNullFilter_returnsIssues() {
            Long projectId = 1L;
            when(projectRepository.existsById(projectId)).thenReturn(true);

            Issue issue = new Issue(10L, "Bug 1", "Desc", new Date(System.currentTimeMillis()),
                    PriorityEnum.HIGH, StateEnum.TODO, TypeEnum.BUG, creatorUser, sampleProject);
            when(issueRepository.findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class)))
                    .thenReturn(List.of(issue));

            List<Issue> result = issueService.getIssues(projectId, null, null, null);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Bug 1", result.get(0).getTitle());
            verify(issueRepository, times(1)).findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class));
        }

        @Test
        @DisplayName("getIssues applica ordinamento ASC con proprietà consentita ('priority')")
        void getIssues_validProject_customAscSortWithAllowedProperty_returnsIssues() {
            Long projectId = 1L;
            when(projectRepository.existsById(projectId)).thenReturn(true);

            Issue issue = new Issue(10L, "Bug 1", "Desc", new Date(System.currentTimeMillis()),
                    PriorityEnum.HIGH, StateEnum.TODO, TypeEnum.BUG, creatorUser, sampleProject);
            when(issueRepository.findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class)))
                    .thenReturn(List.of(issue));

            List<Issue> result = issueService.getIssues(projectId, null, "priority", "asc");

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(issueRepository, times(1)).findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class));
        }

        @Test
        @DisplayName("getIssues gestisce sortDir case-insensitive ('ASC')")
        void getIssues_validProject_caseInsensitiveAscSort_returnsAscDirection() {
            Long projectId = 1L;
            when(projectRepository.existsById(projectId)).thenReturn(true);
            when(issueRepository.findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class)))
                    .thenReturn(Collections.emptyList());

            List<Issue> result = issueService.getIssues(projectId, null, "title", "ASC");

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(issueRepository, times(1)).findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class));
        }

        @Test
        @DisplayName("getIssues effettua fallback su 'creationDate' se la proprietà di sort non è in whitelist")
        void getIssues_validProject_invalidSortProperty_fallsBackToCreationDate() {
            Long projectId = 1L;
            when(projectRepository.existsById(projectId)).thenReturn(true);
            when(issueRepository.findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class)))
                    .thenReturn(Collections.emptyList());

            List<Issue> result = issueService.getIssues(projectId, null, "nonExistingField", "desc");

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(issueRepository, times(1)).findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class));
        }

        @Test
        @DisplayName("getIssues con filtri completi interroga correttamente il repository")
        void getIssues_validProject_withCompleteFilter_returnsFilteredIssues() {
            Long projectId = 1L;
            IssueFilterDto filter = new IssueFilterDto(
                    TypeEnum.BUG,
                    StateEnum.TODO,
                    PriorityEnum.HIGH,
                    2L,
                    5L
            );

            when(projectRepository.existsById(projectId)).thenReturn(true);
            when(issueRepository.findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class)))
                    .thenReturn(Collections.emptyList());

            List<Issue> result = issueService.getIssues(projectId, filter, "state", "desc");

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(projectRepository, times(1)).existsById(projectId);
            verify(issueRepository, times(1)).findAll(ArgumentMatchers.<Specification<Issue>>any(), any(Sort.class));
        }
    }

}
