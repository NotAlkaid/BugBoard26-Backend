package org.ingsw2526_036.bugboard26backend.dtos;

import java.sql.Date;
import lombok.Data;

@Data
public class CommentResponseDto {
    private Long id;
    private String body;
    private Date date;
    private Long creatorId;
    private String creatorUsername;
    private Long issueId;
}
