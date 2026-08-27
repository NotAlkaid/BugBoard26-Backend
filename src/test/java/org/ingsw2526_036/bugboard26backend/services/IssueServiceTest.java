package org.ingsw2526_036.bugboard26backend.services;

import org.ingsw2526_036.bugboard26backend.dtos.IssueFilterDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueRequestDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
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
import org.springframework.security.access.AccessDeniedException;

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
    private BaseUser assigneeUser;
    private BaseUser otherUser;
    private Administrator adminUser;

    @BeforeEach
    void setUp() {
        creatorUser = new BaseUser();
        creatorUser.setId(1L);
        creatorUser.setUsername("creatorUser");
        creatorUser.setEmail("creator@test.com");

        assigneeUser = new BaseUser();
        assigneeUser.setId(2L);
        assigneeUser.setUsername("assigneeUser");
        assigneeUser.setEmail("assignee@test.com");

        otherUser = new BaseUser();
        otherUser.setId(3L);
        otherUser.setUsername("otherUser");
        otherUser.setEmail("other@test.com");

        adminUser = new Administrator();
        adminUser.setId(99L);
        adminUser.setUsername("adminUser");
        adminUser.setEmail("admin@test.com");

        sampleProject = new Project();
        sampleProject.setId(1L);
        sampleProject.setName("Sample Project");
        sampleProject.setCreator(adminUser);
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

    /**
     * Test per il metodo modifyIssue(Long issueId, IssueRequestDto dto, User requester)
     *
     * Strategia di Test: R-WECT (Robust Weak Equivalence Class Testing)
     *
     * 1. Analisi delle Classi di Equivalenza (CE) sui parametri e logica di business:
     *    1) issueId:
     *       - [V_id]              ID issue esistente nel DB.
     *       - [NV_id]             ID issue non presente nel DB -> lancia ResourceNotFoundException.
     *    2) dto (IssueRequestDto):
     *       - [V1_dto]            DTO valido senza etichette (labelIds == null).
     *       - [V2_dto]            DTO valido con etichette (labelIds popolato da ID validi).
     *       - [NV_dto]            DTO nullo o con campi non validi: gestito e bloccato a monte
     *                             nel Controller tramite @Valid (HTTP 400 Bad Request).
     *    3) requester (User) e permessi per stato dell'issue:
     *       - Stato TO-DO (iniziale):
     *         - [V_initial_creator]   Requester è il Creator -> Modifica consentita.
     *         - [V_initial_admin]     Requester è Administrator -> Modifica consentita.
     *         - [NV_initial_unauth]   Requester non è né Creator né Admin -> AccessDeniedException.
     *       - Stato INPROGRESS:
     *         - [V_inp_assignee]      Requester è l'Assignee -> Modifica consentita.
     *         - [V_inp_admin]         Requester è Administrator -> Modifica consentita.
     *         - [NV_inp_creator]      Requester è Creator ma non Assignee -> AccessDeniedException.
     *       - Stato CLOSED:
     *         - [V_closed_admin]      Requester è Administrator -> Modifica consentita.
     *         - [NV_closed_user]      Requester non è Admin -> AccessDeniedException.
     *       - Utente non autenticato: bloccato a monte da Spring Security (filtro JWT -> HTTP 401 Unauthorized).
     *
     * 2. Classi di Equivalenza effettivamente coperte nel Service (4 NV, 8 V):
     *    - issueId   : [V_id], [NV_id]
     *    - dto       : [V1_dto], [V2_dto]
     *    - Permessi  : 5 classi [V] e 3 classi [NV] per le transizioni di stato (TO-DO, INPROGRESS, CLOSED)
     *    => formula R-WECT : 4 (NV) + max(1, 2, 5) (V) = 9 test
     *
     * 3. Casi di test implementati (R-WECT / Single Fault Assumption):
     *    - Test 1 [NV_id]                  : Issue inesistente -> modifyIssue_issueNotFound_throwsResourceNotFoundException
     *    - Test 2 [V_id + TO-DO + Creator] : Stato TO-DO, requester Creator (senza label) -> modifyIssue_initialState_requesterIsCreator_successWithoutLabels
     *    - Test 3 [V_id + TO-DO + Admin]   : Stato TO-DO, requester Admin (con label) -> modifyIssue_initialState_requesterIsAdmin_successWithLabels
     *    - Test 4 [V_id + TO-DO + NonAuth] : Stato TO-DO, utente non autorizzato -> modifyIssue_initialState_unauthorizedUser_throwsAccessDeniedException
     *    - Test 5 [V_id + INP + Assignee]  : Stato INPROGRESS, requester Assignee -> modifyIssue_inProgressState_requesterIsAssignee_success
     *    - Test 6 [V_id + INP + Creator]   : Stato INPROGRESS, requester Creator (non Assignee) -> modifyIssue_inProgressState_requesterIsCreatorNotAssignee_throwsAccessDeniedException
     *    - Test 7 [V_id + INP + Admin]     : Stato INPROGRESS, requester Admin -> modifyIssue_inProgressState_requesterIsAdmin_success
     *    - Test 8 [V_id + CLOSED + NonAdm] : Stato CLOSED, requester non Admin -> modifyIssue_closedState_requesterNotAdmin_throwsAccessDeniedException
     *    - Test 9 [V_id + CLOSED + Admin]  : Stato CLOSED, requester Admin -> modifyIssue_closedState_requesterIsAdmin_success
     */
    @Nested
    @DisplayName("Test per il metodo modifyIssue")
    class ModifyIssueTests {

        @Test
        @DisplayName("modifyIssue lancia ResourceNotFoundException se l'issue non esiste")
        void modifyIssue_issueNotFound_throwsResourceNotFoundException() {
            Long issueId = 999L;
            IssueRequestDto dto = new IssueRequestDto();

            when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> issueService.modifyIssue(issueId, dto, creatorUser)
            );

            assertEquals("Issue not found with id: " + issueId, exception.getMessage());
            verify(issueRepository, times(1)).findById(issueId);
            verifyNoInteractions(issueMapper);
            verifyNoInteractions(labelRepository);
        }

        @Test
        @DisplayName("modifyIssue in stato TO-DO: successo quando il richiedente è il Creator")
        void modifyIssue_initialState_requesterIsCreator_successWithoutLabels() {
            Long issueId = 10L;
            IssueRequestDto dto = new IssueRequestDto();
            dto.setTitle("Updated Title");
            dto.setDescription("Updated Description");
            dto.setLabelIds(null);

            Issue existingIssue = new Issue(issueId, "Old Title", "Old Desc",
                    new Date(System.currentTimeMillis()), PriorityEnum.LOW, StateEnum.TODO,
                    TypeEnum.QUESTION, creatorUser, sampleProject);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(existingIssue));
            when(issueRepository.save(existingIssue)).thenReturn(existingIssue);

            Issue result = issueService.modifyIssue(issueId, dto, creatorUser);

            assertNotNull(result);
            verify(issueMapper, times(1)).updateIssueFromDto(dto, existingIssue);
            verify(labelRepository, never()).findAllById(any());
            verify(issueRepository, times(1)).save(existingIssue);
        }

        @Test
        @DisplayName("modifyIssue in stato TO-DO: successo quando il richiedente è Admin (anche con labelIds)")
        void modifyIssue_initialState_requesterIsAdmin_successWithLabels() {
            Long issueId = 10L;
            Set<Long> labelIds = Set.of(5L);
            IssueRequestDto dto = new IssueRequestDto();
            dto.setTitle("Admin Update");
            dto.setLabelIds(labelIds);

            Issue existingIssue = new Issue(issueId, "Old Title", "Old Desc",
                    new Date(System.currentTimeMillis()), PriorityEnum.LOW, StateEnum.TODO,
                    TypeEnum.BUG, creatorUser, sampleProject);

            Label label = new Label("Security", "#112233");
            label.setId(5L);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(existingIssue));
            when(labelRepository.findAllById(labelIds)).thenReturn(List.of(label));
            when(issueRepository.save(existingIssue)).thenReturn(existingIssue);

            Issue result = issueService.modifyIssue(issueId, dto, adminUser);

            assertNotNull(result);
            verify(issueMapper, times(1)).updateIssueFromDto(dto, existingIssue);
            verify(labelRepository, times(1)).findAllById(labelIds);
            assertTrue(existingIssue.getLabels().contains(label));
            verify(issueRepository, times(1)).save(existingIssue);
        }

        @Test
        @DisplayName("modifyIssue in stato TO-DO: lancia AccessDeniedException se l'utente non è né Creator né Admin")
        void modifyIssue_initialState_unauthorizedUser_throwsAccessDeniedException() {
            Long issueId = 10L;
            IssueRequestDto dto = new IssueRequestDto();

            Issue existingIssue = new Issue(issueId, "Title", "Desc",
                    new Date(System.currentTimeMillis()), PriorityEnum.LOW, StateEnum.TODO,
                    TypeEnum.BUG, creatorUser, sampleProject);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(existingIssue));

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> issueService.modifyIssue(issueId, dto, otherUser)
            );

            assertEquals("Solo il creatore o un amministratore possono modificare una issue in stato TODO.", exception.getMessage());
            verify(issueRepository, never()).save(any());
        }

        @Test
        @DisplayName("modifyIssue in stato INPROGRESS: successo quando il richiedente è l'Assignee")
        void modifyIssue_inProgressState_requesterIsAssignee_success() {
            Long issueId = 10L;
            IssueRequestDto dto = new IssueRequestDto();
            dto.setTitle("Fixing bug");

            Issue existingIssue = new Issue(issueId, "Title", "Desc",
                    new Date(System.currentTimeMillis()), PriorityEnum.MEDIUM, StateEnum.INPROGRESS,
                    TypeEnum.BUG, creatorUser, sampleProject);
            existingIssue.setAssignedTo(assigneeUser);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(existingIssue));
            when(issueRepository.save(existingIssue)).thenReturn(existingIssue);

            Issue result = issueService.modifyIssue(issueId, dto, assigneeUser);

            assertNotNull(result);
            verify(issueMapper, times(1)).updateIssueFromDto(dto, existingIssue);
            verify(issueRepository, times(1)).save(existingIssue);
        }

        @Test
        @DisplayName("modifyIssue in stato INPROGRESS: lancia AccessDeniedException se il richiedente è solo Creator ma non Assignee")
        void modifyIssue_inProgressState_requesterIsCreatorNotAssignee_throwsAccessDeniedException() {
            Long issueId = 10L;
            IssueRequestDto dto = new IssueRequestDto();

            Issue existingIssue = new Issue(issueId, "Title", "Desc",
                    new Date(System.currentTimeMillis()), PriorityEnum.MEDIUM, StateEnum.INPROGRESS,
                    TypeEnum.BUG, creatorUser, sampleProject);
            existingIssue.setAssignedTo(assigneeUser);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(existingIssue));

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> issueService.modifyIssue(issueId, dto, creatorUser)
            );

            assertEquals("Solo l'assegnatario o un amministratore possono modificare una issue in stato INPROGRESS.", exception.getMessage());
            verify(issueRepository, never()).save(any());
        }

        @Test
        @DisplayName("modifyIssue in stato INPROGRESS: successo quando il richiedente è Admin (anche se non assignee)")
        void modifyIssue_inProgressState_requesterIsAdmin_success() {
            Long issueId = 10L;
            IssueRequestDto dto = new IssueRequestDto();

            Issue existingIssue = new Issue(issueId, "Title", "Desc",
                    new Date(System.currentTimeMillis()), PriorityEnum.MEDIUM, StateEnum.INPROGRESS,
                    TypeEnum.BUG, creatorUser, sampleProject);
            existingIssue.setAssignedTo(assigneeUser);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(existingIssue));
            when(issueRepository.save(existingIssue)).thenReturn(existingIssue);

            Issue result = issueService.modifyIssue(issueId, dto, adminUser);

            assertNotNull(result);
            verify(issueRepository, times(1)).save(existingIssue);
        }

        @Test
        @DisplayName("modifyIssue in stato CLOSED: lancia AccessDeniedException se il richiedente non è Admin")
        void modifyIssue_closedState_requesterNotAdmin_throwsAccessDeniedException() {
            Long issueId = 10L;
            IssueRequestDto dto = new IssueRequestDto();

            Issue existingIssue = new Issue(issueId, "Title", "Desc",
                    new Date(System.currentTimeMillis()), PriorityEnum.HIGH, StateEnum.CLOSED,
                    TypeEnum.BUG, creatorUser, sampleProject);
            existingIssue.setAssignedTo(assigneeUser);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(existingIssue));

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> issueService.modifyIssue(issueId, dto, assigneeUser)
            );

            assertEquals("Solo un amministratore può modificare una issue chiusa.", exception.getMessage());
            verify(issueRepository, never()).save(any());
        }

        @Test
        @DisplayName("modifyIssue in stato CLOSED: successo se il richiedente è Admin")
        void modifyIssue_closedState_requesterIsAdmin_success() {
            Long issueId = 10L;
            IssueRequestDto dto = new IssueRequestDto();

            Issue existingIssue = new Issue(issueId, "Title", "Desc",
                    new Date(System.currentTimeMillis()), PriorityEnum.HIGH, StateEnum.CLOSED,
                    TypeEnum.BUG, creatorUser, sampleProject);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(existingIssue));
            when(issueRepository.save(existingIssue)).thenReturn(existingIssue);

            Issue result = issueService.modifyIssue(issueId, dto, adminUser);

            assertNotNull(result);
            verify(issueMapper, times(1)).updateIssueFromDto(dto, existingIssue);
            verify(issueRepository, times(1)).save(existingIssue);
        }
    }

}