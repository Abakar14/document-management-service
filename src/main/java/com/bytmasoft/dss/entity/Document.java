package com.bytmasoft.dss.entity;

import com.bytmasoft.common.entities.BaseEntity;
import com.bytmasoft.dss.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "documents")
public class Document extends BaseEntity implements Serializable {

    @Column(unique = true, nullable = false)
    private String fileName;
    private String originalFileName;
    private String filePath;
    private Integer version;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private Long ownerId;

    @Builder.Default
    private boolean deleted = false;

    @Builder.Default
    private boolean isArchived = false;

}