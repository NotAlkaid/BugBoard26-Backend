package org.ingsw2526_036.bugboard26backend.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuePromoteDto {

    @Size(max = 500, message = "Resolution note must not exceed 500 characters")
    private String resolutionNote;
}
