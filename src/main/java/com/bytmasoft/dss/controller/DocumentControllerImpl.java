package com.bytmasoft.dss.controller;


import com.bytmasoft.dss.dto.DocumentDTO;
import com.bytmasoft.dss.enums.DocumentType;
import com.bytmasoft.dss.service.FileSystemStorageServiceImpl;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
@Schema(name ="DM-Service")
@Tag(name = "Documents", description = "Document Management Service for all documents")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/documents", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentControllerImpl implements DocumentController {

    private final FileSystemStorageServiceImpl fileStorageService;

    @Override
    public ResponseEntity<List<DocumentDTO>> getAllDocumentVersions(Long ownerId, DocumentType documentType, Integer version) {
        return ResponseEntity.ok(fileStorageService.getAllDocumentVersions(ownerId, documentType, version));
    }

    @Override
    public ResponseEntity<Page<DocumentDTO>> getAllDocumentsOwners(List<Long> ownerIDs, DocumentType documentType, Integer version, Pageable pageable) {
        return ResponseEntity.ok(fileStorageService.getAllDocumentsOwners(ownerIDs, documentType, version, pageable));
    }

    @Override
    public ResponseEntity<Page<DocumentDTO>> getAllDocuments(Long ownerId, DocumentType documentType, Integer version, Pageable pageable) {
        return ResponseEntity.ok(fileStorageService.getAllDocuments(ownerId, documentType, version, pageable));
    }

    @Override
    public ResponseEntity<DocumentDTO> uploadDocument(MultipartFile file, DocumentType documentType, Long ownerId) throws IOException {
        return ResponseEntity.ok(fileStorageService.uploadDocument(file, documentType, ownerId));

    }

    @Override
    public ResponseEntity<List<DocumentDTO>> uploadDocumentsMiddleComplexHirarchic(List<MultipartFile> files, DocumentType documentType, Long ownerId) throws Exception {
        return ResponseEntity.ok(fileStorageService.uploadDocuments(files, documentType, ownerId));
    }

    @Override
    public ResponseEntity<DocumentDTO> updateDocument(Long documentId, Optional<DocumentType> documentType, Optional<Long> ownerId, Optional<MultipartFile> file) throws Exception {
        return ResponseEntity.ok(fileStorageService.updateDocument(documentId, documentType, ownerId, file));
    }

    @Override
    public ResponseEntity<Resource> downloadDocument(Long ownerId, DocumentType documentType, Integer version) throws IOException {
        Resource file = fileStorageService.downloadDocument(ownerId, documentType, version);
        String fileContentType = Files.probeContentType(Paths.get(file.getURI()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileContentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename())
                .body(file);

    }


    @Override
    public ResponseEntity<Resource> downloadDocument(Long documentId) throws Exception {
        Resource file = fileStorageService.downloadDocument(documentId);
        String fileContentType = Files.probeContentType(Paths.get(file.getURI()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileContentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename())
                .body(file);
    }

    @Override
    public ResponseEntity<List<Resource>> getAllDocumentsAsResource() throws Exception {
        return ResponseEntity.ok(fileStorageService.downloadAllDocumentsAsResource());
    }

    @Override
    public ResponseEntity<Boolean> deleteDocument(Long documentId) throws Exception {
        return ResponseEntity.ok(fileStorageService.deleteDocumentById(documentId));
    }

    @Override
    public ResponseEntity<Boolean> deleteAllDocuments() throws Exception {
        return ResponseEntity.ok(fileStorageService.deleteAllDocuments());
    }



 //   @Operation(summary = "Get all documents", description = "Retrieve all documents")

/*    public ResponseEntity<List<DocumentDTO>> getAllDocumentVersions(Long ownerId, DocumentType documentType) {
        return ResponseEntity.ok(fileStorageService.getAllDocumentVersions(ownerId, documentType));
    }*/



    public ResponseEntity<List<DocumentDTO>> getAllDocumentOwners(Long ownerId) {
        return ResponseEntity.ok(fileStorageService.getAllDocumentOwner(ownerId));
    }




    public ResponseEntity<Page<DocumentDTO>> getAllDocuments(List<Long> ownerIDs, Pageable pageable) {
        return ResponseEntity.ok(fileStorageService.getAllDocumentOwner(ownerIDs, pageable));
    }







}
