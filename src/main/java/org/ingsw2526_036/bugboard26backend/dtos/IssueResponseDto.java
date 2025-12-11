package org.ingsw2526_036.bugboard26backend.dtos;
import java.sql.Date;
import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;

import lombok.Data;

@Data
public class IssueResponseDto {
    private Long id;
    private String title;
    private String description;
    private Date creationDate;
    private byte[] image;
    private PriorityEnum priority;
    private StateEnum state;
    private TypeEnum type;
    private Long creatorId;

}