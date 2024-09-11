package com.bytmasoft.dss.controller;

import com.bytmasoft.dss.dto.DocumentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentController {

    /**
     * simple Upload with one file und 1st level directory hierarchic
     *
     * @param file
     * @param serviceTypes
     * @param fileType
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    ResponseEntity<DocumentDto> uploadDocument(@Parameter(description = "A file for the service type and file type")  @RequestPart("file") MultipartFile file,
                                            @Parameter(description = "Service type ")  @RequestParam("serviceType") String serviceTypes,
                                            @Parameter(description = "file type")  @RequestParam("fileType") String fileType) throws Exception;


    /**
     * middle Upload with list of files und 1st level directory hierarchic
     *
     * @param files
     * @param serviceType
     * @param fileType
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/uploads",  consumes = "multipart/form-data")
    ResponseEntity<List<DocumentDto>> uploadDocumentsMiddleComplexHirarchic(@RequestParam("file") List<MultipartFile> files,
                                                                         @RequestParam("serviceType") String serviceType,
                                                                         @RequestParam("fileType") String fileType) throws Exception;

    /**
     * complex Upload with list of files und complex level directories hierarchic
     *
     * @param files
     * @param fileTypes
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/uploads/properties", consumes = "multipart/form-data")
    ResponseEntity<List<DocumentDto>> uploadDocumentComplexDirectoriesHierachic(@Parameter(description = "A file for the service type and file type")  @RequestPart("files") List<MultipartFile> files,
                                                                             @Parameter(description = "file type")  @RequestParam("fileTypes") String...fileTypes) throws Exception;





    @PutMapping(consumes = "multipart/form-data")
    ResponseEntity<DocumentDto> updateDocument(@RequestParam("documentId") String documentId, @RequestParam("file") MultipartFile file) throws Exception;

    @Operation(summary = "Download a document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File found",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping(value = "/downloand/{documentId}")
    ResponseEntity<Resource> downloadDocument(@RequestParam("documentId") String documentName) throws Exception;

    ResponseEntity<List<Resource>> getAllDocuments() throws Exception;

    @GetMapping("/service/type")
    ResponseEntity<List<Resource>> getAllDocumentsByServiceType(@RequestParam("serviceType") String serviceType) throws Exception;

    @GetMapping("/service/type/file/type")
    ResponseEntity<Resource> getAllDocumentsByServiceTypeAndFileType(@RequestParam("serviceType") String serviceType, @RequestParam("fileType") String fileType) throws Exception;

    @DeleteMapping("/document")
    ResponseEntity<String> deleteDocument(@RequestParam("documentId") String documentId) throws Exception;

    /**
     * only for admin Please note delete hole documents
     * We will archive the docuemtns and then remove it
     * @return
     * @throws Exception
     */
    @DeleteMapping
    ResponseEntity<String> deleteAllDocuments() throws Exception;



}