package com.bytmasoft.dss.dto;

import com.bytmasoft.dss.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentCreateDto {
    private Long ownerId;
    private Long schoolId;
    private MultipartFile document;
    private DocumentType documentType;
}
