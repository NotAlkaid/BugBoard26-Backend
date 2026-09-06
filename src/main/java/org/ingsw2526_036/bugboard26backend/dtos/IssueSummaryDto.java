package org.ingsw2526_036.bugboard26backend.dtos;

public record IssueSummaryDto(
        long total,
        long open,
        long bugs,
        long closed
) {}
