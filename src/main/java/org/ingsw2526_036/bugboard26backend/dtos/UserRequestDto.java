package org.ingsw2526_036.bugboard26backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "Username is mandatory")
    private String username;
    @Email(message = "Email should be valid")
    @NotBlank
    private String email;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @Pattern(regexp = "^(?i)(ADMIN|BASEUSER)$", message = "Type must be ADMIN or BASEUSER")
    private String type;
}