package org.ingsw2526_036.bugboard26backend.dtos;
import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IssueRequestDto {

    @NotBlank(message = "Issue title is mandatory")
    private String title;
    @NotBlank(message = "Issue description is mandatory")
    private String description;
    private byte[] image;
    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "Priority must be HIGH, MEDIUM, or LOW")
    private String priority;
    @Pattern(regexp = "BUG|FEATURE|QUESTION|DOCUMENTATION", message = "Type must be BUG, FEATURE, QUESTION, or DOCUMENTATION")
    private String type;



}