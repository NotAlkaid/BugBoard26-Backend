package org.ingsw2526_036.bugboard26backend.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationRequestDto {
    private String email;
    private String password;
}
