package com.bytmasoft.dss.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileStorageService {
//    String init();

    String saveFile(MultipartFile file, String serviceType, String fileType) throws IOException;

    Resource loadFileAsResource(String fileName);

    Path load(String filename);

    List<String> saveFiles(List<MultipartFile> files, String serviceType, String fileType);

    String updateDocument(String documentId, MultipartFile file);

    Resource getDocument(String documentId);

    List<Resource> loadAll();

    boolean deleteAll();


}
