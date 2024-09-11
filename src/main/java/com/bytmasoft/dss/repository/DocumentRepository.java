package com.bytmasoft.dss.repository;

import com.bytmasoft.dss.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByfileName(String fileName);
}
