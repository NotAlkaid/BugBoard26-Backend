package org.ingsw2526_036.bugboard26backend.dtos;

import lombok.Data;

@Data
public class ProjectResponseDto {
    private Long id;
    private String name;
    private Long creatorId;
    private String creatorUsername;
    private byte[] icon;
}
