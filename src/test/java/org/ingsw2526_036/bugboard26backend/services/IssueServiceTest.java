package org.ingsw2526_036.bugboard26backend.services;

import org.ingsw2526_036.bugboard26backend.dtos.IssueFilterDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.BaseUser;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Label;
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
import java.util.Optional;
import java.util.Set;

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

    /**
     * Test per il metodo createIssue(Long projectId, IssueRequestDto dto, User creator)
     *
     * Strategia di Test: R-WECT (Robust Weak Equivalence Class Testing)
     *
     * 1. Analisi delle Classi di Equivalenza (CE) sui parametri:
     *    1) projectId:
     *       - [V_proj]   ID progetto esistente nel DB.
     *       - [NV_proj]  ID progetto non presente nel DB -> lancia ResourceNotFoundException.
     *    2) issueRequestDto:
     *       - [V1_dto]   DTO valido con labelIds == null (nessuna etichetta).
     *       - [V2_dto]   DTO valido con labelIds vuoto (labelIds.isEmpty() == true).
     *       - [V3_dto]   DTO valido con labelIds popolato da ID validi (associazione etichette).
     *       - [NV_dto]   DTO nullo o con campi non validi: gestito e bloccato a monte
     *                    nel Controller tramite @Valid (che verifica i vincoli @NotBlank e @Pattern).
     *    3) creator:
     *       - [V_user]   Utente autenticato iniettato dal contesto di sicurezza (@AuthenticationPrincipal).
     *       - [NV_user]  Utente non autenticato / token assente o non valido: bloccato a monte
     *                    da Spring Security (filtro JWT -> HTTP 401 Unauthorized).
     *
     * 2. Classi di Equivalenza effettivamente coperte nel Service (1 NV, 5 V):
     *    - projectId       : [V_proj], [NV_proj]
     *    - issueRequestDto : [V1_dto], [V2_dto], [V3_dto] (Max classi V = 3)
     *    - creator         : [V_user]
     *    => Formula R-WECT : 1 (NV) + max(1, 3, 1) (V) = 4 Test
     *
     * 3. Casi di test implementati (R-WECT / Single Fault Assumption):
     *    - Test 1 [NV_proj + V1_dto + V_user] : Progetto non trovato -> createIssue_projectNotFound_throwsResourceNotFoundException
     *    - Test 2 [V_proj + V1_dto + V_user]  : Senza etichette (null) -> createIssue_withoutLabels_nullLabelIds_success
     *    - Test 3 [V_proj + V2_dto + V_user]  : Con lista etichette vuota -> createIssue_withoutLabels_emptyLabelIds_success
     *    - Test 4 [V_proj + V3_dto + V_user]  : Con etichette valide associate -> createIssue_withValidLabels_success
     */
    @Nested
    @DisplayName("Test per il metodo createIssue")
    class CreateIssueTests {

        @Test
        @DisplayName("createIssue lancia ResourceNotFoundException se il progetto non esiste")
        void createIssue_projectNotFound_throwsResourceNotFoundException() {
            Long projectId = 999L;
            IssueRequestDto dto = new IssueRequestDto();
            dto.setTitle("New Issue");
            dto.setDescription("Description");

            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> issueService.createIssue(projectId, dto, creatorUser)
            );

            assertEquals("Project not found with id: " + projectId, exception.getMessage());
            verify(projectRepository, times(1)).findById(projectId);
            verifyNoInteractions(issueMapper);
            verifyNoInteractions(labelRepository);
            verifyNoInteractions(issueRepository);
        }

        @Test
        @DisplayName("createIssue crea con successo una issue senza etichette (labelIds == null)")
        void createIssue_withoutLabels_nullLabelIds_success() {
            Long projectId = 1L;
            IssueRequestDto dto = new IssueRequestDto();
            dto.setTitle("Bug without labels");
            dto.setDescription("Description");
            dto.setLabelIds(null);

            Issue mappedIssue = new Issue();
            mappedIssue.setTitle(dto.getTitle());
            mappedIssue.setDescription(dto.getDescription());

            Issue savedIssue = new Issue(100L, dto.getTitle(), dto.getDescription(),
                    new Date(System.currentTimeMillis()), PriorityEnum.MEDIUM, StateEnum.TODO,
                    TypeEnum.BUG, creatorUser, sampleProject);

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));
            when(issueMapper.toEntity(dto)).thenReturn(mappedIssue);
            when(issueRepository.save(mappedIssue)).thenReturn(savedIssue);

            Issue result = issueService.createIssue(projectId, dto, creatorUser);

            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertEquals(creatorUser, mappedIssue.getCreator());
            assertEquals(sampleProject, mappedIssue.getProject());
            verify(labelRepository, never()).findAllById(any());
            verify(issueRepository, times(1)).save(mappedIssue);
        }

        @Test
        @DisplayName("createIssue crea con successo una issue con lista etichette vuota (labelIds.isEmpty())")
        void createIssue_withoutLabels_emptyLabelIds_success() {
            Long projectId = 1L;
            IssueRequestDto dto = new IssueRequestDto();
            dto.setTitle("Bug empty labels");
            dto.setDescription("Description");
            dto.setLabelIds(Collections.emptySet());

            Issue mappedIssue = new Issue();
            mappedIssue.setTitle(dto.getTitle());

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));
            when(issueMapper.toEntity(dto)).thenReturn(mappedIssue);
            when(issueRepository.save(mappedIssue)).thenReturn(mappedIssue);

            Issue result = issueService.createIssue(projectId, dto, creatorUser);

            assertNotNull(result);
            verify(labelRepository, never()).findAllById(any());
            verify(issueRepository, times(1)).save(mappedIssue);
        }

        @Test
        @DisplayName("createIssue associa correttamente le etichette indicate (labelIds popolato)")
        void createIssue_withValidLabels_success() {
            Long projectId = 1L;
            Set<Long> labelIds = Set.of(10L, 20L);

            IssueRequestDto dto = new IssueRequestDto();
            dto.setTitle("Bug with labels");
            dto.setDescription("Description");
            dto.setLabelIds(labelIds);

            Issue mappedIssue = new Issue();
            mappedIssue.setTitle(dto.getTitle());

            Label label1 = new Label("Frontend", "#FF0000");
            label1.setId(10L);
            Label label2 = new Label("Backend", "#00FF00");
            label2.setId(20L);

            when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject));
            when(issueMapper.toEntity(dto)).thenReturn(mappedIssue);
            when(labelRepository.findAllById(labelIds)).thenReturn(List.of(label1, label2));
            when(issueRepository.save(mappedIssue)).thenReturn(mappedIssue);

            Issue result = issueService.createIssue(projectId, dto, creatorUser);

            assertNotNull(result);
            assertEquals(2, mappedIssue.getLabels().size());
            assertTrue(mappedIssue.getLabels().contains(label1));
            assertTrue(mappedIssue.getLabels().contains(label2));
            verify(labelRepository, times(1)).findAllById(labelIds);
            verify(issueRepository, times(1)).save(mappedIssue);
        }
    }

}
