package org.ingsw2526_036.bugboard26backend.mappers;

import org.ingsw2526_036.bugboard26backend.dtos.ProjectRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.ProjectResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    //mapping dto to entity while ignoring id and creator fields
    @Mapping(target = "id", ignore = true) //ignoro id perchè è generato automaticamente
    @Mapping(target = "creator", ignore = true) //ignoro creator perchè dal frontend mi arriva solo l'id e poi lo setto nel service
    @Mapping(target = "issues", ignore = true) //ignoro issues perchè un progetto appena creato non ha issues
    @Mapping(target = "participants", ignore = true) //ignoro members perchè un progetto appena creato non ha membri
    Project toEntity(ProjectRequestDto dto);

    @Mapping(target = "creatorId", source = "creator.id") 
    @Mapping(target = "creatorUsername", source = "creator.realUsername")
    ProjectResponseDto toDto(Project project);
}
