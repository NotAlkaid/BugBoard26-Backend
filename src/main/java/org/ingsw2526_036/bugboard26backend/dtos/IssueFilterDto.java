package org.ingsw2526_036.bugboard26backend.dtos;

import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;

public record IssueFilterDto(
        TypeEnum type,
        StateEnum state,
        PriorityEnum priority,
        Long assignedToId,
        Long labelId
) {}
