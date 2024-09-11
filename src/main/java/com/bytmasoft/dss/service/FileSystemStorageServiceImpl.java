package com.bytmasoft.dss.service;

import com.bytmasoft.dss.config.StorageProperties;
import com.bytmasoft.dss.dto.DocumentDto;
import com.bytmasoft.dss.entity.Document;
import com.bytmasoft.dss.exception.StorageException;
import com.bytmasoft.dss.exception.StorageFileNotFoundException;
import com.bytmasoft.dss.mapper.DocumentMapper;
import com.bytmasoft.dss.repository.DocumentRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class FileSystemStorageServiceImpl implements FileStorageService {

    private final StorageProperties storageProperties;
    private final DocumentMapper documentMapper;
    private final DocumentRepository documentRepository;
    private Path rootLocation;

    public FileSystemStorageServiceImpl(StorageProperties storageProperties, DocumentMapper documentMapper, DocumentRepository documentRepository) {
        this.storageProperties = storageProperties;
        this.rootLocation = Paths.get(storageProperties.getUpload().getLocation());
        this.documentMapper = documentMapper;
        this.documentRepository = documentRepository;
        init();
    }

    @Override
    public void init() {
        try {
            Path path = Files.createDirectories(rootLocation);
        } catch (Exception e) {
            throw new StorageException("Could not initialize storage ", e);
        }
    }


    private DocumentDto saveStandard(MultipartFile file, String serviceType, String fileType, Path serviceDir) throws IOException {

        String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
        Path targetLocation = null;

        if (serviceDir == null || serviceDir.toString().isEmpty()) {
            Path serviceDirlocal = Paths.get(rootLocation.toString(), serviceType, fileType);
            Files.createDirectories(serviceDirlocal);
            targetLocation = serviceDirlocal.resolve(fileName);

        } else if (serviceDir != null) {
            Files.createDirectories(serviceDir);
            targetLocation = serviceDir.resolve(fileName);
        }
        Files.copy(file.getInputStream(), targetLocation);
        Document document = getDocumentResponse(fileName, targetLocation.toString());
        document.setFileName(fileName);
        document.setFilePath(targetLocation.toString().substring(0, targetLocation.toString().lastIndexOf("/")));
        //TODO  take a user from ContextHolder
        document.setInsertedBy("Abakar");
        documentRepository.save(document);
        return documentMapper.entityToDto(document);

    }

    @Override
    public DocumentDto saveFile(MultipartFile file, String serviceType, String fileType) throws IOException {
        return saveStandard(file, serviceType, fileType, null);
    }

    @Override
    public DocumentDto saveFile(MultipartFile file, String... fileTypes) throws IOException {
        Path serviceDir = Paths.get(rootLocation.toString(), fileTypes);
        return saveStandard(file, null, null, serviceDir);

    }

    @Override
    public List<DocumentDto> saveComplexFiles(List<MultipartFile> files, String... fileTypes) {
        Path serviceDir = Paths.get(rootLocation.toString(), fileTypes);
        List<DocumentDto> documentResponses = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                documentResponses.add(saveStandard(file, null, null, serviceDir));
            } catch (Exception e) {
                throw new StorageException("Could not save file " + file.getOriginalFilename(), e);
            }

        }
        return documentResponses;
    }

    @Override
    public List<DocumentDto> saveFiles(List<MultipartFile> files, String serviceType, String fileType) {
        List<DocumentDto> documentResponses = new ArrayList<>();
        files.forEach(file -> {
            try {
                documentResponses.add(saveStandard(file, serviceType, fileType, null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return documentResponses;
    }


    @Override
    public DocumentDto updateDocument(String documentName, MultipartFile file) throws IOException {
        //TODO Before do update we need to archive the table Document
        // document_archive table to save all changes in it
        Document document = documentRepository.findByfileName(documentName).orElseThrow(() -> new StorageFileNotFoundException("Document with name : " + documentName + " not found "));
        String newFileName = UUID.randomUUID().toString() + file.getOriginalFilename();
        Path filePath = Paths.get(document.getFilePath() + "/" + newFileName);
        Files.copy(file.getInputStream(), filePath);

        document.setFileName(newFileName);
        //TODO take a username form ContextHolder
        document.setUpdatedBy("Abakar");
        documentRepository.save(document);

        return documentMapper.entityToDto(document);
    }

    @Override
    public Resource loadFileAsResource(String fileName) throws IOException {
        try {
            Path filePath = load(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file : " + fileName, e);
        }

    }

    @Override
    public Path load(String filename) throws IOException {

        return Files.walk(Paths.get(rootLocation.toString()))
                .filter(path -> path.getFileName().toString().equals(filename))
                .findFirst()
                .orElseThrow(() -> new StorageFileNotFoundException("File " + filename + " not found"));
    }

    @Override
    public Resource getDocument(String documentName) throws IOException {
        Path filePath = load(documentName);
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new StorageFileNotFoundException("Could not read file " + documentName);
        }

    }

    @Override
    public ResponseEntity<Resource> getDocumentByName(String documentName) throws IOException {

        try {

            Path filePath = load(documentName);
            Resource resource = new UrlResource(filePath.toUri());
            String fileContentType = Files.probeContentType(filePath);
            if(fileContentType ==  null) {
                fileContentType = "application/octet-stream";
            }
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fileContentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +resource.getFilename())
                        .body(resource);
            }else {
                throw new StorageFileNotFoundException("Could not read file " + documentName);
            }
        }catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        } catch (IOException ex) {
            return ResponseEntity.status(500).build();
        }
    }



    @Override
    public List<Resource> loadAll() {
        return List.of();
    }

    @Override
    public boolean deleteAll() {
        return FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public String deleteDocumentByName(String documentId) {
        return "";
    }

    @Override
    public String deleteAllDocument() {
        return "";
    }


    private Document getDocumentResponse(String fileName, String filePath) {
        return Document.builder()
                .fileName(fileName)
                .filePath(filePath)
                .build();

    }

}
