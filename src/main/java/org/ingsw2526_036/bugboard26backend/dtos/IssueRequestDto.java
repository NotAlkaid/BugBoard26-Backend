package org.ingsw2526_036.bugboard26backend.dtos;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IssueRequestDto {

    @NotBlank(message = "Issue title is mandatory")
    @Size(max = 255, message = "Issue title must not exceed 255 characters")
    private String title;
    @NotBlank(message = "Issue description is mandatory")
    @Size(max = 255, message = "Issue description must not exceed 255 characters")
    private String description;
    private byte[] image;
    @Pattern(regexp = "HIGH|MEDIUM|LOW", message = "Priority must be HIGH, MEDIUM, or LOW")
    private String priority;
    @Pattern(regexp = "BUG|FEATURE|QUESTION|DOCUMENTATION", message = "Type must be BUG, FEATURE, QUESTION, or DOCUMENTATION")
    private String type;
    @Size(max = 10, message = "An issue cannot have more than 10 labels")
    private Set<Long> labelIds;
}