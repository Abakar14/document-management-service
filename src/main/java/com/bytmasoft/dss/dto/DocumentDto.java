package com.bytmasoft.dss.dto;


import com.bytmasoft.dss.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {

    private Long id;
    private Long ownerId;
    private String fileName;
    private String filePath;
    private Integer version;
    private String addedBy;
    private String modifiedBy;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime addedOn;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modifiedOn;
    private String originalFileName;
    private boolean deleted;
    private boolean isArchived;

}
