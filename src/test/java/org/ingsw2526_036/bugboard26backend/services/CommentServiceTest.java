package org.ingsw2526_036.bugboard26backend.services;

import org.ingsw2526_036.bugboard26backend.dtos.CommentRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.CommentResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Administrator;
import org.ingsw2526_036.bugboard26backend.entities.BaseUser;
import org.ingsw2526_036.bugboard26backend.entities.Comment;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import org.ingsw2526_036.bugboard26backend.exception.ResourceNotFoundException;
import org.ingsw2526_036.bugboard26backend.mappers.CommentMapper;
import org.ingsw2526_036.bugboard26backend.repositories.CommentRepository;
import org.ingsw2526_036.bugboard26backend.repositories.IssueRepository;
import org.ingsw2526_036.bugboard26backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private Project sampleProject;
    private Project otherProject;
    private BaseUser creatorUser;
    private Administrator adminUser;
    private Issue sampleIssue;

    @BeforeEach
    void setUp() {
        adminUser = new Administrator();
        adminUser.setId(99L);
        adminUser.setUsername("adminUser");
        adminUser.setEmail("admin@test.com");

        sampleProject = new Project();
        sampleProject.setId(1L);
        sampleProject.setName("Sample Project");
        sampleProject.setCreator(adminUser);

        otherProject = new Project();
        otherProject.setId(2L);
        otherProject.setName("Other Project");
        otherProject.setCreator(adminUser);

        creatorUser = new BaseUser();
        creatorUser.setId(10L);
        creatorUser.setUsername("creatorUser");
        creatorUser.setEmail("creator@test.com");
        creatorUser.setJoinedProjects(new ArrayList<>(List.of(sampleProject)));

        sampleIssue = new Issue(100L, "Sample Bug", "Description of bug",
                new Date(System.currentTimeMillis()), PriorityEnum.HIGH, StateEnum.TODO,
                TypeEnum.BUG, creatorUser, sampleProject);
    }

    @Nested
    @DisplayName("Test per il metodo addComment")
    class AddCommentTests {

        @Test
        @DisplayName("addComment lancia ResourceNotFoundException se l'issue non esiste")
        void addComment_issueNotFound_throwsResourceNotFoundException() {
            Long projectId = 1L;
            Long issueId = 999L;
            CommentRequestDto dto = new CommentRequestDto();
            dto.setBody("Test comment");

            when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> commentService.addComment(projectId, issueId, dto, creatorUser)
            );

            assertEquals("Issue not found with id: " + issueId, exception.getMessage());
            verify(issueRepository, times(1)).findById(issueId);
            verifyNoInteractions(commentMapper);
            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("addComment lancia IllegalArgumentException se l'issue non appartiene al projectId fornito")
        void addComment_issueProjectIdMismatch_throwsIllegalArgumentException() {
            Long mismatchedProjectId = 2L; // L'issue appartiene al progetto 1L
            Long issueId = 100L;
            CommentRequestDto dto = new CommentRequestDto();
            dto.setBody("Test comment");

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(sampleIssue));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> commentService.addComment(mismatchedProjectId, issueId, dto, creatorUser)
            );

            assertEquals("Issue with id " + issueId + " does not belong to project with id " + mismatchedProjectId, exception.getMessage());
            verify(issueRepository, times(1)).findById(issueId);
            verifyNoInteractions(commentMapper);
            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("addComment lancia AccessDeniedException se l'utente non è Admin e non fa parte del progetto")
        void addComment_userNotAdminAndNotParticipant_throwsAccessDeniedException() {
            Long projectId = 1L;
            Long issueId = 100L;
            CommentRequestDto dto = new CommentRequestDto();
            dto.setBody("Test comment");

            // Utente non admin i cui joinedProjects non includono sampleProject
            BaseUser nonParticipant = new BaseUser();
            nonParticipant.setId(50L);
            nonParticipant.setUsername("stranger");
            nonParticipant.setJoinedProjects(new ArrayList<>(List.of(otherProject)));

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(sampleIssue));
            when(userRepository.findById(nonParticipant.getId())).thenReturn(Optional.of(nonParticipant));

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> commentService.addComment(projectId, issueId, dto, nonParticipant)
            );

            assertEquals("User is not authorized to add comments to this project.", exception.getMessage());
            verify(commentRepository, never()).save(any());
        }

        @Test
        @DisplayName("addComment lancia AccessDeniedException se l'utente non è Admin e joinedProjects è null")
        void addComment_userNotAdminAndJoinedProjectsNull_throwsAccessDeniedException() {
            Long projectId = 1L;
            Long issueId = 100L;
            CommentRequestDto dto = new CommentRequestDto();
            dto.setBody("Test comment");

            BaseUser userWithNullProjects = new BaseUser();
            userWithNullProjects.setId(60L);
            userWithNullProjects.setUsername("nullProjectsUser");
            userWithNullProjects.setJoinedProjects(null);

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(sampleIssue));
            when(userRepository.findById(userWithNullProjects.getId())).thenReturn(Optional.of(userWithNullProjects));

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> commentService.addComment(projectId, issueId, dto, userWithNullProjects)
            );

            assertEquals("User is not authorized to add comments to this project.", exception.getMessage());
            verify(commentRepository, never()).save(any());
        }

        @Test
        @DisplayName("addComment salva con successo il commento se l'utente è un partecipante al progetto")
        void addComment_participantUser_success() {
            Long projectId = 1L;
            Long issueId = 100L;
            CommentRequestDto dto = new CommentRequestDto();
            dto.setBody("This is a valid comment");

            Comment mappedComment = new Comment();
            mappedComment.setBody(dto.getBody());

            Comment savedComment = new Comment(1L, dto.getBody(), new Date(System.currentTimeMillis()),
                    creatorUser, sampleIssue);

            CommentResponseDto responseDto = new CommentResponseDto();
            responseDto.setId(1L);
            responseDto.setBody("This is a valid comment");

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(sampleIssue));
            when(userRepository.findById(creatorUser.getId())).thenReturn(Optional.of(creatorUser));
            when(commentMapper.toEntity(dto)).thenReturn(mappedComment);
            when(commentRepository.save(mappedComment)).thenReturn(savedComment);
            when(commentMapper.toDto(savedComment)).thenReturn(responseDto);

            CommentResponseDto result = commentService.addComment(projectId, issueId, dto, creatorUser);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("This is a valid comment", result.getBody());
            assertEquals(creatorUser, mappedComment.getCreator());
            assertEquals(sampleIssue, mappedComment.getIssue());
            verify(commentRepository, times(1)).save(mappedComment);
            verify(commentMapper, times(1)).toDto(savedComment);
        }

        @Test
        @DisplayName("addComment salva con successo il commento se l'utente è Administrator (anche senza joinedProjects)")
        void addComment_adminUser_successEvenIfNotParticipant() {
            Long projectId = 1L;
            Long issueId = 100L;
            CommentRequestDto dto = new CommentRequestDto();
            dto.setBody("Admin comment");

            Comment mappedComment = new Comment();
            mappedComment.setBody(dto.getBody());

            Comment savedComment = new Comment(2L, dto.getBody(), new Date(System.currentTimeMillis()),
                    adminUser, sampleIssue);

            CommentResponseDto responseDto = new CommentResponseDto();
            responseDto.setId(2L);
            responseDto.setBody("Admin comment");

            when(issueRepository.findById(issueId)).thenReturn(Optional.of(sampleIssue));
            when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
            when(commentMapper.toEntity(dto)).thenReturn(mappedComment);
            when(commentRepository.save(mappedComment)).thenReturn(savedComment);
            when(commentMapper.toDto(savedComment)).thenReturn(responseDto);

            CommentResponseDto result = commentService.addComment(projectId, issueId, dto, adminUser);

            assertNotNull(result);
            assertEquals(2L, result.getId());
            assertEquals(adminUser, mappedComment.getCreator());
            assertEquals(sampleIssue, mappedComment.getIssue());
            verify(commentRepository, times(1)).save(mappedComment);
            verify(commentMapper, times(1)).toDto(savedComment);
        }
    }
}