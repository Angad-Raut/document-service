package com.projectx.document_service.services;

import com.projectx.document_service.exceptions.AlreadyExistsException;
import com.projectx.document_service.exceptions.ResourceNotFoundException;
import com.projectx.document_service.payloads.*;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    Boolean addUpdateDocument(DocumentDto dto)throws ResourceNotFoundException,
            AlreadyExistsException, IOException;
    DocumentPageResponseDto getAllDocuments(PageRequestDto dto);
    List<DocumentDropDownDto> getDocumentDropDown();
    byte[] getDocument(EntityStringIdDto dto)throws ResourceNotFoundException;
    Integer documentCount();
    DownloadDocDto downloadDocument(EntityStringIdDto dto)throws ResourceNotFoundException;
}
