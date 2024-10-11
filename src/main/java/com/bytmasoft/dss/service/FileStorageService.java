package com.bytmasoft.dss.service;

import com.bytmasoft.common.exception.StorageException;
import com.bytmasoft.common.exception.StorageFileNotFoundException;
import com.bytmasoft.dss.dto.DocumentDTO;
import com.bytmasoft.dss.entity.Document;
import com.bytmasoft.dss.enums.DocumentType;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FileStorageService {

    void init();

    DocumentDTO getDocumentById(Long documentId) throws StorageFileNotFoundException;

    List<DocumentDTO> getAllDocumentVersions(Long ownerId, DocumentType documentType, Integer version);

//    List<DocumentDTO> getAllDocumentOwner(Long ownerId);

    Page<DocumentDTO> getAllDocumentOwner(List<Long> ownerIDs, Pageable pageable);

    List<DocumentDTO> uploadDocuments(List<MultipartFile> files, DocumentType documentType, Long ownerId) throws IOException;

    DocumentDTO uploadDocument(MultipartFile file, DocumentType documentType, Long ownerId) throws IOException;

    DocumentDTO updateDocument(Long documentId, Optional<DocumentType> documentType, Optional<Long> ownerId ,Optional<MultipartFile> file) throws IOException;

    Resource downloadDocument(Long documentId) throws IOException;

    Page<DocumentDTO> getAllDocuments(Long ownerId, DocumentType documentType, Integer version, Pageable pageable);

    Resource downloadDocument(Long ownerId, DocumentType documentType, Integer version) throws IOException;

    List<Resource> downloadAllDocumentsAsResource();

    Boolean softDeleteDocument(Long documentId);

    boolean permanentlyDeleteDocument(Long documentId);

    boolean archiveDocument(Long documentId) throws StorageException;

    boolean archiveDocumentToS3(Long documentId);

    boolean deleteAllDocuments();

    Page<DocumentDTO> getAllDocumentsOwners(List<Long> ownerIDs, DocumentType documentType, Integer version, Pageable pageable);


    //    Path load(String filename) throws IOException;
//    Resource loadFileAsResource(String fileName) throws IOException;

//    DocumentDTO saveFile(MultipartFile file, String serviceType, String fileTypes) throws IOException;

//    List<DocumentDTO> saveFiles(List<MultipartFile> files, String serviceType, String fileType);

//    DocumentDTO saveFile(MultipartFile file, String... fileTypes) throws IOException;

//    List<DocumentDTO> saveComplexFiles(List<MultipartFile> files, String... fileTypes);

}
