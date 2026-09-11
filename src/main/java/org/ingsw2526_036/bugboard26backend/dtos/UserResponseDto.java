package org.ingsw2526_036.bugboard26backend.dtos;

import org.ingsw2526_036.bugboard26backend.enums.RoleEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    @Schema(implementation = RoleEnum.class)
    private String type;
}