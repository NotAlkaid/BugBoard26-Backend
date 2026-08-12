package org.ingsw2526_036.bugboard26backend.mappers;

import java.util.Set;

import org.ingsw2526_036.bugboard26backend.dtos.LabelRequestDto;
import org.ingsw2526_036.bugboard26backend.dtos.LabelResponseDto;
import org.ingsw2526_036.bugboard26backend.entities.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "issues", ignore = true)
    Label toEntity(LabelRequestDto dto);

    LabelResponseDto toDto(Label entity);

    Set<LabelResponseDto> toDtoSet(Set<Label> entities);
}