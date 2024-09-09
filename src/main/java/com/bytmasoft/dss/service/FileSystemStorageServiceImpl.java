package com.bytmasoft.dss.service;

import com.bytmasoft.dss.config.StorageProperties;
import com.bytmasoft.dss.exception.StorageException;
import com.bytmasoft.dss.exception.StorageFileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
    private  Path rootLocation;

    public FileSystemStorageServiceImpl(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        this.rootLocation = Paths.get(storageProperties.getUpload().getLocation());

    }

    @Override
    public String saveFile(MultipartFile file, String serviceType, String fileType) throws IOException {
        String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
        Path serviceDir = Paths.get(rootLocation.toString(), serviceType, fileType);
        Files.createDirectories(serviceDir);
        Path targetLocation = serviceDir.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation);
        return fileName;
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = load(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }else {
                throw new StorageFileNotFoundException("Could not read file " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file : "+fileName, e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public List<String> saveFiles(List<MultipartFile> files, String serviceType, String fileType) {
        List<String> fileNames = new ArrayList<>();
        files.forEach(file -> {
            try {
                String name =     saveFile(file,  serviceType, fileType);
                fileNames.add(name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return fileNames;
    }


    @Override
    public String updateDocument(String documentId, MultipartFile file) {
        return "";
    }

    @Override
    public Resource getDocument(String documentId) {
        return null;
    }

    @Override
    public List<Resource> loadAll() {
        return List.of();
    }

    @Override
    public boolean deleteAll() {
       return FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

}
