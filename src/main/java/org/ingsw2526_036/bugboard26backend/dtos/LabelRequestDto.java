package org.ingsw2526_036.bugboard26backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LabelRequestDto {
    @NotBlank(message = "Label name is mandatory")
    private String name;
    private String color;
}
