package com.bytmasoft.dss.controller;

import com.bytmasoft.common.exception.StorageFileNotFoundException;
import com.bytmasoft.dss.dto.DocumentDTO;
import com.bytmasoft.dss.enums.DocumentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DocumentController {


    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long documentId) throws StorageFileNotFoundException;


    @GetMapping("/owners")
    public ResponseEntity<Page<DocumentDTO>> getAllDocumentsOwners(@RequestParam List<Long> ownerIDs,
                                                             @RequestParam(required = false) DocumentType documentType,
                                                             @RequestParam(required = false) Integer version,
                                                             @ParameterObject
                                                                       @Parameter(description = "Pagination information", required = false, schema = @Schema(implementation = Pageable.class))
                                                                       @PageableDefault(page = 0, size = 10, sort = {"id", "fileName"}, direction = Sort.Direction.ASC) Pageable pageable);

    @GetMapping
    public ResponseEntity<Page<DocumentDTO>> getAllDocuments(@RequestParam(required = false) Long ownerId,
                                                             @RequestParam(required = false) DocumentType documentType,
                                                             @RequestParam(required = false) Integer version,
                                                             @ParameterObject
                                                                       @Parameter(description = "Pagination information", required = false, schema = @Schema(implementation = Pageable.class))
                                                                       @PageableDefault(page = 0, size = 10, sort = {"id", "fileName"}, direction = Sort.Direction.ASC) Pageable pageable);


    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @Parameter(description = "A file for a documentType") @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam("ownerId") Long ownerId) throws IOException;

    /**
     * @param files
     * @param documentType
     * @param ownerId
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/uploads", consumes = "multipart/form-data")
    ResponseEntity<List<DocumentDTO>> uploadDocumentsMiddleComplexHirarchic(@RequestParam("file") List<MultipartFile> files,
                                                                            @RequestParam("documentType") DocumentType documentType,
                                                                            @RequestParam("ownerId") Long ownerId) throws Exception;


    @PutMapping(consumes = "multipart/form-data")
    ResponseEntity<DocumentDTO> updateDocument(@RequestParam("documentId") Long documentId,
                                               @RequestParam(value = "documentType") Optional<DocumentType> documentType,
                                               @RequestParam(value = "ownerId") Optional<Long> ownerId,
                                               @RequestParam(value = "file") Optional<MultipartFile> file) throws Exception;



    @PutMapping(value = "/{documentId}/archive")
    ResponseEntity<Boolean> archiveDocument(@PathVariable Long documentId) throws Exception;




    @Operation(summary = "Download a document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File found",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping(value = "/{documentId}/download")
    ResponseEntity<Resource> downloadDocument(@PathVariable("documentId") Long documentId) throws Exception;


    @GetMapping("/owners/{ownerId}/{documentType}/{version}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("ownerId") Long ownerId,
                                                     @PathVariable("documentType") DocumentType documentType,
                                                     @PathVariable("version") Integer version) throws IOException;




    ResponseEntity<List<Resource>> getAllDocumentsAsResource() throws Exception;


    @DeleteMapping("/{documentId}")
    ResponseEntity<Boolean> deleteDocument(@PathVariable("documentId") Long documentId) throws Exception;

    /**
     * only for admin Please note delete hole documents
     * We will archive the docuemtns and then remove it
     *
     * @return
     * @throws Exception
     */
    @DeleteMapping
    ResponseEntity<Boolean> deleteAllDocuments() throws Exception;


    /**
     * complex Upload with list of files und complex level directories hierarchic
     */
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