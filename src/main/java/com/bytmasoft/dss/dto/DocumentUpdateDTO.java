package com.bytmasoft.dss.dto;


import com.bytmasoft.dss.enums.DocumentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class DocumentUpdateDTO implements Serializable {

    private Long ownerId;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;


}
