package com.bytmasoft.dss.controller;


import com.bytmasoft.dss.dto.DocumentDto;
import com.bytmasoft.dss.service.FileSystemStorageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/documents")
public class DocumentControllerImpl implements DocumentController {

    private final FileSystemStorageServiceImpl fileStorageService;

    @Override
    public ResponseEntity<Resource> downloadDocument(String documentName) throws Exception {
        Resource file = fileStorageService.loadFileAsResource(documentName);
        String fileContentType = Files.probeContentType(Paths.get(file.getURI()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileContentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename())
                .body(file);
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
    public ResponseEntity<DocumentDto> uploadDocument(MultipartFile file, String serviceType, String fileType) throws Exception {
        return ResponseEntity.ok(fileStorageService.saveFile(file, serviceType, fileType));
    }

    @Override
    public ResponseEntity<List<DocumentDto>> uploadDocumentsMiddleComplexHirarchic(List<MultipartFile> files, String serviceType, String fileType) throws Exception {
        return ResponseEntity.ok(fileStorageService.saveFiles(files, serviceType, fileType));
    }

    @Override
    public ResponseEntity<List<DocumentDto>> uploadDocumentComplexDirectoriesHierachic(List<MultipartFile> files, String... fileTypes) throws Exception {
        return ResponseEntity.ok(fileStorageService.saveComplexFiles(files, fileTypes));
    }

    @Override
    public ResponseEntity<DocumentDto> updateDocument(String documentId, MultipartFile file) throws Exception {
        return ResponseEntity.ok(fileStorageService.updateDocument(documentId, file));
    }


    @Override
    public ResponseEntity<String> deleteDocument(String documentId) throws Exception {
        return ResponseEntity.ok(fileStorageService.deleteDocumentByName(documentId));
    }

    @Override
    public ResponseEntity<String> deleteAllDocuments() throws Exception {
        return ResponseEntity.ok(fileStorageService.deleteAllDocument());
    }
}
