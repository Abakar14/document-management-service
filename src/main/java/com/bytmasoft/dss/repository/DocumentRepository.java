package com.bytmasoft.dss.repository;

import com.bytmasoft.dss.entity.Document;
import com.bytmasoft.dss.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByfileName(String fileName);
    List<Document> findByOwnerId(Long ownerId);
    List<Document> findByDocumentType(DocumentType documentType);
    List<Document> findByOwnerIdAndDocumentType(Long ownerId, DocumentType documentType);
}
