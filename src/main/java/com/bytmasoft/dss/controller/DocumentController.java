package com.bytmasoft.dss.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentController {

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    ResponseEntity<String> uploadDocument( @Parameter(description = "A file for the service type and file type")  @RequestPart("file") MultipartFile file,
                                           @Parameter(description = "Service type ")  @RequestParam("serviceType") String serviceType,
                                           @Parameter(description = "file type")  @RequestParam("fileType") String fileType) throws Exception;

    @PostMapping(value = "/uploads",  consumes = "multipart/form-data")
    ResponseEntity<List<String>> uploadDocument(@RequestParam("file") List<MultipartFile> files,
                                          @RequestParam("serviceType") String serviceType,
                                          @RequestParam("fileType") String fileType) throws Exception;


    @PutMapping(consumes = "multipart/form-data")
    ResponseEntity<String> updateDocument(@RequestParam("documentId") String documentId, @RequestParam("file") MultipartFile file) throws Exception;

    @GetMapping(value = "/{documentId}")
    ResponseEntity<Resource> getDocument(@RequestParam("documentId") String documentId) throws Exception;

    ResponseEntity<List<Resource>> getAllDocuments() throws Exception;

    @GetMapping("/service/type")
    ResponseEntity<List<Resource>> getAllDocumentsByServiceType(@RequestParam("serviceType") String serviceType) throws Exception;

    @GetMapping("/service/type/file/type")
    ResponseEntity<Resource> getAllDocumentsByServiceTypeAndFileType(@RequestParam("serviceType") String serviceType, @RequestParam("fileType") String fileType) throws Exception;

    @DeleteMapping(value = "/onedocument")
    ResponseEntity<String> deleteDocument(@RequestParam("documentId") String documentId) throws Exception;

    /**
     * only for admin Please note delete hole documents
     * We will archive the docuemtns and then remove it
     * @return
     * @throws Exception
     */
    ResponseEntity<String> deleteAllDocuments() throws Exception;



}