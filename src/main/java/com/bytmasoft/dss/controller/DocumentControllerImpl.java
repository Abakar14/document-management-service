package com.bytmasoft.dss.controller;


import com.bytmasoft.dss.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/documents")
public class DocumentControllerImpl implements DocumentController{

    private final FileStorageService fileStorageService;

    @Override
    public ResponseEntity<String> uploadDocument(MultipartFile file, String serviceType, String fileType) throws Exception {
        return ResponseEntity.ok(fileStorageService.saveFile(file, serviceType, fileType));
    }

    @Override
    public ResponseEntity<List<String>> uploadDocument(List<MultipartFile> files, String serviceType, String fileType) throws Exception {
        return ResponseEntity.ok(fileStorageService.saveFiles(files, serviceType, fileType));
    }

    @Override
    public ResponseEntity<String> updateDocument(String documentId, MultipartFile file) throws Exception {
        return ResponseEntity.ok(fileStorageService.updateDocument(documentId, file));
    }

    @Override
    public ResponseEntity<Resource> getDocument(String documentId) throws Exception {
        return ResponseEntity.ok(fileStorageService.getDocument(documentId));
    }

    @Override
    public ResponseEntity<List<Resource>> getAllDocuments() throws Exception {
        return ResponseEntity.ok(fileStorageService.loadAll());
    }

    @Override
    public ResponseEntity<List<Resource>> getAllDocumentsByServiceType(String serviceType) throws Exception {
        return null;
    }

    @Override
    public ResponseEntity<Resource> getAllDocumentsByServiceTypeAndFileType(String serviceType, String fileType) throws Exception {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteDocument(String documentId) throws Exception {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteAllDocuments() throws Exception {
        return null;
    }
}
