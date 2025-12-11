package org.ingsw2526_036.bugboard26backend.mappers;

import org.ingsw2526_036.bugboard26backend.dtos.IssueRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.IssueResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IssueMapper {

    @Mapping(target = "id", ignore = true) //ignoro id perchè è generato automaticamente
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "creationDate", ignore = true) //ignoro creationDate perchè è generato automaticamente
    @Mapping(target = "image", source = "image")
    @Mapping(target = "priority", source = "priority")
    @Mapping(target = "state", constant = "TODO") //setto lo stato di default a OPEN
    @Mapping(target = "creator", ignore = true) //ignoro creator perchè dal frontend mi arriva solo l'id e poi lo setto nel service
    @Mapping(target = "assignedTo", ignore = true) //ignoro assignee perchè dal frontend mi arriva solo l'id e poi lo setto nel service
    @Mapping(target = "project", ignore = true) //ignoro project perchè dal frontend mi arriva solo l'id e poi lo setto nel service
    @Mapping(target = "type", source = "type")
    Issue toEntity(IssueRequestDto dto);

    @Mapping(target = "creatorId", source = "creator.id")
    IssueResponseDto toDto(Issue issue);

}
