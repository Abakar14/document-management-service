package com.bytmasoft.dss.service;

import com.bytmasoft.common.exception.StorageException;
import com.bytmasoft.common.exception.StorageFileNotFoundException;
import com.bytmasoft.dss.config.StorageProperties;
import com.bytmasoft.dss.dto.DocumentDTO;
import com.bytmasoft.dss.entity.Document;
import com.bytmasoft.dss.enums.DocumentType;
import com.bytmasoft.dss.mapper.DocumentMapper;
import com.bytmasoft.dss.repository.DocumentRepository;
import com.bytmasoft.dss.repository.DocumentSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class FileSystemStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class);

    private final DocumentSpecification documentSpecification;
    private final StorageProperties storageProperties;
    private final DocumentMapper documentMapper;
    private final DocumentRepository documentRepository;
    private final FileValidationService fileValidationService;
    private final StringHttpMessageConverter stringHttpMessageConverter;

    private String documentFolder = "/documents/";
    private  String trashFolder = "/trash/";
    private  String archiveFolder = "/documents/archive/";
    private Path rootLocation;

    /**
     *
     * @param storageProperties
     * @param documentMapper
     * @param documentRepository
     * @param fileValidationService
     * @param stringHttpMessageConverter
     */
    public FileSystemStorageService(DocumentSpecification documentSpecification, StorageProperties storageProperties, DocumentMapper documentMapper, DocumentRepository documentRepository, FileValidationService fileValidationService, StringHttpMessageConverter stringHttpMessageConverter) {
        this.documentSpecification = documentSpecification;
        this.storageProperties = storageProperties;
        this.rootLocation = Paths.get(storageProperties.getUpload().getLocation());
        this.documentMapper = documentMapper;
        this.documentRepository = documentRepository;
        this.fileValidationService = fileValidationService;
        init();
        this.stringHttpMessageConverter = stringHttpMessageConverter;
    }


    public void init() {
        try {
            Path path = Files.createDirectories(rootLocation);
        } catch (Exception e) {
            throw new StorageException("Could not initialize storage ", e);
        }
    }


    public DocumentDTO getDocumentById(Long documentId) throws StorageFileNotFoundException{
        return documentMapper.entityToDto(getDocument(documentId));
    }

    public DocumentDTO uploadDocument(MultipartFile file, DocumentType documentType, Long ownerId) throws IOException {

        fileValidationService.isSupportedFileType(file);

        // Check if a previous version exists for this document type and owner
        List<Document> existingDocuments = documentRepository.findByOwnerIdAndDocumentType(ownerId, documentType);
        int newVersion = existingDocuments.isEmpty() ? 1 : existingDocuments.get(existingDocuments.size() - 1).getVersion() + 1;

        Map<String, String> storedFileProperties = storeFile(file, documentType, ownerId, newVersion);

        // Save document metadata
        Document document = Document.builder()
                .fileName(storedFileProperties.get("fileName"))
                .documentType(documentType)
                .ownerId(ownerId)
                .originalFileName(storedFileProperties.get("originalFilename"))
                .filePath(storedFileProperties.get("filePath"))
                .insertedBy(getUsername())
                .version(newVersion)
                .build();
        return documentMapper.entityToDto(documentRepository.save(document));
    }


    public List<DocumentDTO> uploadDocuments(List<MultipartFile> files, DocumentType documentType, Long ownerId) throws IOException {
        List<DocumentDTO> documentDTOS = new ArrayList<>();
        for (MultipartFile file : files) {
           documentDTOS.add(uploadDocument(file, documentType, ownerId));
        }
        return documentDTOS;
    }


    public List<DocumentDTO> getAllDocumentVersions(Long ownerId, DocumentType documentType, Integer version) {
        return documentRepository.findAll(documentSpecification.getDocumentsByDocumentTypeAndVersion(ownerId, documentType, version)).stream().map(documentMapper::entityToDto).collect(Collectors.toUnmodifiableList());

    }

/*    @Override
    public List<DocumentDTO> getAllDocumentOwner(Long ownerId) {
        return   documentRepository.findByOwnerId(ownerId).stream().map(documentMapper::entityToDto).collect(Collectors.toUnmodifiableList());

    }*/


    public Page<DocumentDTO> getAllDocumentOwner(List<Long> ownerIDs, Pageable pageable) {
        return documentRepository.findByOwnerIdIn(ownerIDs,  pageable)
                .map(documentMapper::entityToDto);
    }

    public Page<DocumentDTO> getAllDocumentsOwners(List<Long> ownerIDs, DocumentType documentType, Integer version, Pageable pageable) {

        Specification<Document> specification = documentSpecification.getDocumentsOwnerListByDocumentTypeAndVersion(ownerIDs, documentType, version);
       return documentRepository.findAll(specification, pageable).map(documentMapper::entityToDto);

    }




    public Page<DocumentDTO> getAllDocuments(Long ownerId, DocumentType documentType, Integer version, Pageable pageable) {
        Specification<Document> specification = documentSpecification.getDocumentsByDocumentTypeAndVersion(ownerId, documentType, version);
        return documentRepository.findAll(specification, pageable).map(documentMapper::entityToDto);
    }


    public Resource downloadDocument(Long ownerId, DocumentType documentType, Integer version) throws IOException {
        Document document = getDocument(ownerId, documentType, version);
        return loadFileAsResource(document.getFileName());
    }


    public Resource downloadDocument(Long documentId) throws IOException {
        Document document = getDocument(documentId);
        return loadFileAsResource(document.getFileName());
    }

    //TODO Before do update we need to archive the table Document
    // document_archive table to save all changes in it


    public DocumentDTO updateDocument(Long documentId, Optional<DocumentType> documentType, Optional<Long> ownerId, Optional<MultipartFile> file) throws IOException {

        Document document = getDocument(documentId);
        if(documentType.isPresent()){
            document.setDocumentType(documentType.get());
        }
        if(ownerId.isPresent()){
            document.setOwnerId(ownerId.get());
        }
        //TODO build the logic if the file not exisit i need to change following
        //1- change the existing file name with new info
        //2- move the file to new filepath
        //3- move the original file name to new document

        if(file.isPresent()){
            return this.uploadDocument(file.get(), document.getDocumentType(), document.getOwnerId());
        }

        return documentMapper.entityToDto(documentRepository.save(document));

    }


    public List<Resource> downloadAllDocumentsAsResource() {
        return List.of();
    }


    public Boolean softDeleteDocument(Long documentId) {
        logger.info("Attempting to soft delete document with ID: {}", documentId);
        try {
            Document document = getDocument(documentId);
            document.setDeleted(true);
            moveFileToTrash(document.getFilePath());
            logger.info("Document with ID: {} soft deleted successfully", documentId);
            return true;
        } catch (Exception e) {
            throw new StorageException(e.getMessage()+" Error by soft delete document", e);
        }

    }


    public boolean permanentlyDeleteDocument(Long documentId) {

        try {
            Document document = getDocument(documentId);
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);
            documentRepository.delete(document);
            return true;
        } catch (Exception e) {
            throw new StorageException(e.getMessage()+" Error by permanently delete document", e);
        }
    }


    public boolean archiveDocument(Long documentId) throws StorageException{
        logger.info("Archiving document with ID: {}", documentId);
        try {
            Document document = getDocument(documentId);
            if(document.isArchived()){
                throw new StorageException("Archive document with ID: "+documentId+" is already archived");
            }
            document.setFilePath(moveFileToArchive(document));
            document.setArchived(true);
            documentRepository.save(document);
            logger.info("Document with ID: {} archived successfully", documentId);
            return true;
        }catch (Exception e){
            throw new StorageException(e.getMessage()+" Error by archive document", e);
        }

    }

    //Save to AWS S3

    public boolean archiveDocumentToS3(Long documentId) {
        try {
           /* Document document = getDocument(documentId);

            // Upload the file to S3 and get the file's URL
            String s3Url = s3Service.uploadFileToS3(document.getFilePath());

            // Update the document metadata to point to the S3 URL
            document.setFilePath(s3Url);
            documentRepository.save(document);

            // Optionally, delete the local copy after successful upload
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);*/
            return false;

        }catch (Exception e){
           throw  new StorageException(e.getMessage()+" Error by archive document to AWS S3", e);
        }

    }


    public boolean deleteAllDocuments() {
//        return FileSystemUtils.deleteRecursively(rootLocation.toFile();
        return false;
    }




    /**
     * ************************ private Section ************************************
     */

    private String moveFileToArchive(Document document ) throws IOException {
        Path archiveDirectory = Paths.get(storageProperties.getUpload().getLocation()+archiveFolder+"/"+document.getDocumentType().toString().toLowerCase());
        if(!Files.exists(archiveDirectory)){
            Files.createDirectories(archiveDirectory);
        }
        Path source = Paths.get(document.getFilePath()+"/"+document.getFileName());
        Files.move(source, archiveDirectory.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        return archiveDirectory.toString()+"/"+source.getFileName();

    }
    private void moveFileToTrash(String filePath) throws IOException {
        Path source = Paths.get(filePath);
        Path trashDirectory = Paths.get(storageProperties.getUpload().getLocation()+trashFolder);
        Files.createDirectories(trashDirectory);
        Files.move(source, trashDirectory.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
//        Files.move(source, trashDirectory, StandardCopyOption.REPLACE_EXISTING);

    }

     private Document getDocument(Long documentId) throws StorageFileNotFoundException{
        return  documentRepository.findById(documentId)
                .orElseThrow(() -> new StorageFileNotFoundException("Document with Id: "+documentId +" not found"));

    }

    private Document getDocument(Long ownerId, DocumentType documentType,Integer version) {
        return  documentRepository.findByOwnerIdAndDocumentTypeAndVersion(ownerId, documentType, version)
                .orElseThrow(() -> new StorageFileNotFoundException("Document with ownerId: "+ownerId + " documentType: " +documentType+" and version: "+version +" not found"));

    }
    private Map<String, String> storeFile(MultipartFile file, DocumentType documentType, Long ownerId, int newVersion) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String directoryPath = storageProperties.getUpload().getLocation()+ this.documentFolder +documentType.toString().toLowerCase();
        Files.createDirectories(Paths.get(directoryPath));

        String fileName = generateFileName(file, documentType, ownerId, newVersion);
        Path filePath = Paths.get(directoryPath, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Map <String, String> storeFileProperties = new HashMap<>();
        storeFileProperties.put("fileName", fileName);
        storeFileProperties.put("filePath", directoryPath);
        storeFileProperties.put("originalFilename", originalFilename);


        return storeFileProperties;
    }
    private String generateFileName(MultipartFile file, DocumentType documentType, Long userId, int newVersion) {
        String extension = getFileExtention(file.getOriginalFilename());
        return "user_id_"+userId+"_"+documentType.toString().toLowerCase()+"_v"+newVersion+"."+extension;

    }

    private String getFileExtention(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".")+1);
    }

    private DocumentDTO saveStandard(MultipartFile file, String serviceType, String fileType, Path serviceDir) throws IOException {

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
        document.setInsertedBy(getUsername());

        documentRepository.save(document);
        return documentMapper.entityToDto(document);

    }

    /**
     *
     * @param fileName
     * @param filePath
     * @return
     */
    private Document getDocumentResponse(String fileName, String filePath) {
        return Document.builder()
                .fileName(fileName)
                .filePath(filePath)
                .build();

    }
    private String getUsername() {
      /*  return SecurityContextHolder.getContext().getAuthentication().getName();*/
        return "Abakar";
    }
    private Resource loadFileAsResource(String fileName) throws IOException {
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

    private Path load(String filename) throws IOException {

        return Files.walk(Paths.get(rootLocation.toString()))
                .filter(path -> path.getFileName().toString().equals(filename))
                .findFirst()
                .orElseThrow(() -> new StorageFileNotFoundException("File " + filename + " not found"));
    }

    /******************************************** Delete Section *******************************/

/*    @Override
   */
/*    @Override
    public DocumentDTO saveFile(MultipartFile file, String... fileTypes) throws IOException {
        Path serviceDir = Paths.get(rootLocation.toString(), fileTypes);
        return saveStandard(file, null, null, serviceDir);

    }*/
/*    @Override
    public Resource getDocumentAsResource(String documentName) throws IOException {
        Path filePath = load(documentName);
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new StorageFileNotFoundException("Could not read file " + documentName);
        }



/*    @Override
    public Resource getDocumentByName(String documentName) throws IOException {

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
    }*/


/*    @Override
    public DocumentDTO updateDocument(Long documentId, MultipartFile file) throws IOException {
        //TODO Before do update we need to archive the table Document
        // document_archive table to save all changes in it
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new StorageFileNotFoundException("Document with name : " + documentId + " not found "));
        String newFileName = UUID.randomUUID().toString() + file.getOriginalFilename();
        Path filePath = Paths.get(document.getFilePath() + "/" + newFileName);
        Files.copy(file.getInputStream(), filePath);

        document.setFileName(newFileName);
        //TODO take a username form ContextHolder
        document.setUpdatedBy("Abakar");
        documentRepository.save(document);

        return documentMapper.entityToDto(document);
    }*/

    /*@Override
    public DocumentDTO saveFile(MultipartFile file, String serviceType, String fileType) throws IOException {
        return saveStandard(file, serviceType, fileType, null);
    }
*/

   /* @Override
    public List<DocumentDTO> saveComplexFiles(List<MultipartFile> files, String... fileTypes) {
        Path serviceDir = Paths.get(rootLocation.toString(), fileTypes);
        List<DocumentDTO> documentResponses = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                documentResponses.add(saveStandard(file, null, null, serviceDir));
            } catch (Exception e) {
                throw new StorageException("Could not save file " + file.getOriginalFilename(), e);
            }

        }
        return documentResponses;
    }*/

/*    @Override
    public List<DocumentDTO> saveFiles(List<MultipartFile> files, String serviceType, String fileType) {
        List<DocumentDTO> documentResponses = new ArrayList<>();
        files.forEach(file -> {
            try {
                documentResponses.add(saveStandard(file, serviceType, fileType, null));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return documentResponses;
    }*/
}
