package org.ingsw2526_036.bugboard26backend.dtos;

import lombok.Data;

@Data
public class ProjectRequestDto {
    //@NotBlank(message = "Project name is mandatory")
    private String name;
    //@NotBlank(message = "Id of the creator is mandatory")
    private Long creatorId;
    //@NotBlank(message = "Project icon is mandatory")
    private byte[] icon;
}
