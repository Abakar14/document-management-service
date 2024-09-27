package com.bytmasoft.dss.service;

import com.bytmasoft.dss.config.StorageProperties;
import com.bytmasoft.dss.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@Service
public class FileValidationService {

    private final StorageProperties storageProperties;



    public boolean isSupportedFileType(MultipartFile file) {

        if(file.isEmpty()){
            throw new StorageException("File is empty");
        }

        String extension = getFileExtension(file);

        if(!storageProperties.getDocument().getSupportedFileTypes().contains(extension)){
            throw new StorageException("Unsupported file type\"");
        }

        return true;

    }

    private String getFileExtension(MultipartFile file) {
        return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") +1);
    }

}
