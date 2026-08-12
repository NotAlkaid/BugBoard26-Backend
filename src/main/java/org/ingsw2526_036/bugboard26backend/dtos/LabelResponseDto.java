package org.ingsw2526_036.bugboard26backend.dtos;

import lombok.Data;

@Data
public class LabelResponseDto {
    private Long id;
    private String name;
    private String color;
}
