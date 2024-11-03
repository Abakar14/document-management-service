package com.bytmasoft.dss.controller;

import com.bytmasoft.common.exception.StorageFileNotFoundException;
import com.bytmasoft.dss.dto.DocumentDTO;
import com.bytmasoft.dss.enums.DocumentType;
import com.bytmasoft.dss.service.FileSystemStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;



@Schema(name ="DM-Service")
@Tag(name = "Documents", description = "Document Management Service for all documents")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/documents", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentController {

    private final FileSystemStorageService fileStorageService;

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long documentId) throws StorageFileNotFoundException{
        return ResponseEntity.ok(fileStorageService.getDocumentById(documentId));
    }

    @GetMapping("/owners")
    public ResponseEntity<Page<DocumentDTO>> getAllDocumentsOwners(@RequestParam List<Long> ownerIDs,
                                                                   @RequestParam(required = false) DocumentType documentType,
                                                                   @RequestParam(required = false) Integer version,
                                                                   @ParameterObject
                                                                   @Parameter(description = "Pagination information", required = false, schema = @Schema(implementation = Pageable.class))
                                                                   @PageableDefault(page = 0, size = 10, sort = {"id", "fileName"}, direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(fileStorageService.getAllDocumentsOwners(ownerIDs, documentType, version, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<DocumentDTO>> getAllDocuments(@RequestParam(required = false) Long ownerId,
                                                             @RequestParam(required = false) DocumentType documentType,
                                                             @RequestParam(required = false) Integer version,
                                                             @ParameterObject
                                                             @Parameter(description = "Pagination information", required = false, schema = @Schema(implementation = Pageable.class))
                                                             @PageableDefault(page = 0, size = 10, sort = {"id", "fileName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(fileStorageService.getAllDocuments(ownerId, documentType, version, pageable));
    }


    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @Parameter(description = "A file for a documentType") @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam("ownerId") Long ownerId) throws IOException{
        return ResponseEntity.ok(fileStorageService.uploadDocument(file, documentType, ownerId));

    }

    /**
     * @param files
     * @param documentType
     * @param ownerId
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/uploads", consumes = "multipart/form-data")
    public ResponseEntity<List<DocumentDTO>> uploadDocumentsMiddleComplexHirarchic(@RequestParam("file") List<MultipartFile> files,
                                                                            @RequestParam("documentType") DocumentType documentType,
                                                                            @RequestParam("ownerId") Long ownerId) throws Exception{
        return ResponseEntity.ok(fileStorageService.uploadDocuments(files, documentType, ownerId));
    }


    @PutMapping(consumes = "multipart/form-data")
    public ResponseEntity<DocumentDTO> updateDocument(@RequestParam("documentId") Long documentId,
                                               @RequestParam(value = "documentType") Optional<DocumentType> documentType,
                                               @RequestParam(value = "ownerId") Optional<Long> ownerId,
                                               @RequestParam(value = "file") Optional<MultipartFile> file) throws Exception {
        return ResponseEntity.ok(fileStorageService.updateDocument(documentId, documentType, ownerId, file));
    }

    @PutMapping(value = "/{documentId}/archive")
    public ResponseEntity<Boolean> archiveDocument(@PathVariable Long documentId) throws Exception{
        return ResponseEntity.ok(fileStorageService.archiveDocument(documentId));
    }

    @GetMapping("/owners/{ownerId}/{documentType}/{version}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("ownerId") Long ownerId,
                                                     @PathVariable("documentType") DocumentType documentType,
                                                     @PathVariable("version") Integer version) throws IOException{
        Resource file = fileStorageService.downloadDocument(ownerId, documentType, version);
        String fileContentType = Files.probeContentType(Paths.get(file.getURI()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileContentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename())
                .body(file);

    }


    @Operation(summary = "Download a document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File found",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping(value = "/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("documentId") Long documentId) throws Exception{
        Resource file = fileStorageService.downloadDocument(documentId);
        String fileContentType = Files.probeContentType(Paths.get(file.getURI()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileContentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename())
                .body(file);
    }

    public ResponseEntity<List<Resource>> getAllDocumentsAsResource() throws Exception{
        return ResponseEntity.ok(fileStorageService.downloadAllDocumentsAsResource());
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Boolean> deleteDocument(@PathVariable("documentId") Long documentId) throws Exception{
        return ResponseEntity.ok(fileStorageService.softDeleteDocument(documentId));
    }

    /**
     * only for admin Please note delete hole documents
     * We will archive the docuemtns and then remove it
     *
     * @return
     * @throws Exception
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteAllDocuments() throws Exception{
        return ResponseEntity.ok(fileStorageService.deleteAllDocuments());
    }



 //   @Operation(summary = "Get all documents", description = "Retrieve all documents")

/*    public ResponseEntity<List<DocumentDTO>> getAllDocumentVersions(Long ownerId, DocumentType documentType) {
        return ResponseEntity.ok(fileStorageService.getAllDocumentVersions(ownerId, documentType));
    }*/



/*
    public ResponseEntity<List<DocumentDTO>> getAllDocumentOwners(Long ownerId) {
        return ResponseEntity.ok(fileStorageService.getAllDocumentOwner(ownerId));
    }
*/




    public ResponseEntity<Page<DocumentDTO>> getAllDocuments(List<Long> ownerIDs, Pageable pageable) {
        return ResponseEntity.ok(fileStorageService.getAllDocumentOwner(ownerIDs, pageable));
    }




    /*@PostMapping(value = "/uploads/properties", consumes = "multipart/form-data")
    ResponseEntity<List<DocumentDTO>> uploadDocumentComplexDirectoriesHierachic(@Parameter(description = "A file for the service type and file type")  @RequestPart("files") List<MultipartFile> files,
                                                                                @Parameter(description = "file type")  @RequestParam("fileTypes") String...fileTypes) throws Exception;
*/
  /*  @GetMapping("/service/type")
    ResponseEntity<List<Resource>> getAllDocumentsByServiceType(@RequestParam("serviceType") String serviceType) throws Exception;
*/
   /* @GetMapping("/service/type/file/type")
    ResponseEntity<Resource> getAllDocumentsByServiceTypeAndFileType(@RequestParam("serviceType") String serviceType, @RequestParam("fileType") String fileType) throws Exception;
*/
        /*
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    ResponseEntity<DocumentDTO> uploadDocument(@Parameter(description = "A file for the service type and file type")  @RequestPart("file") MultipartFile file,
                                               @Parameter(description = "Service type ")  @RequestParam("serviceType") String serviceTypes,
                                               @Parameter(description = "file type")  @RequestParam("fileType") String fileType) throws Exception;
*/


}
