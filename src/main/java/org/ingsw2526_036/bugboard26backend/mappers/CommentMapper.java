package org.ingsw2526_036.bugboard26backend.mappers;

import org.ingsw2526_036.bugboard26backend.dtos.CommentRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.CommentResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "issue", ignore = true)
    @Mapping(target = "body", source = "body")
    Comment toEntity(CommentRequestDto dto);

    @Mapping(target = "creatorId", source = "creator.id")
    @Mapping(target = "creatorUsername", source = "creator.realUsername")
    @Mapping(target = "issueId", source = "issue.id")
    CommentResponseDto toDto(Comment comment);
}
