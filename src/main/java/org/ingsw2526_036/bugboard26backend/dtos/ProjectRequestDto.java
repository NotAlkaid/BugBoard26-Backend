package org.ingsw2526_036.bugboard26backend.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProjectRequestDto {
    @NotBlank(message = "Project name is mandatory")
    private String name;
    @NotEmpty(message = "Project icon is mandatory")
    private byte[] icon;
}
