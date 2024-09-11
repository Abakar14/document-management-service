package com.bytmasoft.dss.service;

import com.bytmasoft.dss.dto.DocumentDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileStorageService {
//    String init();


    void init();

    DocumentDto saveFile(MultipartFile file, String serviceType, String fileTypes) throws IOException;

    List<DocumentDto> saveFiles(List<MultipartFile> files, String serviceType, String fileType);

    DocumentDto saveFile(MultipartFile file, String... fileTypes) throws IOException;

    List<DocumentDto> saveComplexFiles(List<MultipartFile> files, String... fileTypes);

    DocumentDto updateDocument(String documentName, MultipartFile file) throws IOException;

    Resource loadFileAsResource(String fileName) throws IOException;

    Path load(String filename) throws IOException;

    Resource getDocument(String documentName) throws IOException;

    ResponseEntity<Resource> getDocumentByName(String documentName) throws IOException;

    List<Resource> loadAll();

    boolean deleteAll();


    String deleteDocumentByName(String documentId);

    String deleteAllDocument();
}
