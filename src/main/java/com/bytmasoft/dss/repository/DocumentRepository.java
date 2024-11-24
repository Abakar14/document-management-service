package com.bytmasoft.dss.repository;

import com.bytmasoft.dss.entity.Document;
import com.bytmasoft.dss.enums.DocumentType;
import com.bytmasoft.dss.enums.OwnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    Optional<Document> findByfileName(String fileName);

    List<Document> findByOwnerId(Long ownerId);

    List<Document> findByDocumentType(DocumentType documentType);

    Optional<Document> findByOwnerIdAndDocumentTypeAndOwnerTypeAndVersion(Long ownerId, DocumentType documentType, OwnerType ownerType, Integer version);

List<Document> findByOwnerIdAndDocumentTypeAndSchoolId(Long ownerId, DocumentType documentType, Long schoolId);

    Page<Document> findByOwnerIdIn(List<Long> ownerIDs, Pageable pageable);
   /* @Query("select d from Document d where d.ownerId in: ids")
    Page<Document> findByOwnerId(@Param("ids") List<Long> ownerIDs, Pageable pageable);*/
}
