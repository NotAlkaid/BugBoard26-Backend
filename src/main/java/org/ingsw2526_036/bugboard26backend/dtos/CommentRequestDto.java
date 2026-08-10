package org.ingsw2526_036.bugboard26backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotBlank(message = "Comment body is mandatory")
    private String body;
}
