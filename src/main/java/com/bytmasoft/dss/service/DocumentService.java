package com.bytmasoft.dss.service;

import com.bytmasoft.dss.entity.Document;
import com.bytmasoft.dss.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentService {

  Document uploadDocument(MultipartFile file, DocumentType documentType, Long ownerId) throws IOException;
}
