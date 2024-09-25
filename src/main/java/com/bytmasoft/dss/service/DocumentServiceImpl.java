package com.bytmasoft.dss.service;

import com.bytmasoft.dss.entity.Document;
import com.bytmasoft.dss.enums.DocumentType;
import com.bytmasoft.dss.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {


    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public Document uploadDocument(MultipartFile file, DocumentType documentType, Long ownerId) throws IOException {
        // Check if a previous version exists for this document type and owner
        List<Document> existingDocuments = documentRepository.findByOwnerIdAndDocumentType(ownerId, documentType);

        int newVersion = existingDocuments.isEmpty() ? 1 : existingDocuments.get(existingDocuments.size() - 1).getVersion() + 1;

        // Store the new version of the file
        String fileName = fileStorageService.storeFile(file, documentType, ownerId, newVersion);

        // Save document metadata
        Document document = new Document();
        document.setFilePath(fileName);
        document.setDocumentType(documentType);
        document.setOwnerId(ownerId);
        document.setVersion(newVersion);
        return documentRepository.save(document);
    }
}
