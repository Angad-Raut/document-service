package com.projectx.document_service.services;

import com.projectx.document_service.exceptions.ResourceNotFoundException;
import com.projectx.document_service.payloads.*;

import java.io.IOException;

public interface CompanyDocumentService {
    Boolean addDocumentByCompanyId(CompanyDocDto dto)
            throws ResourceNotFoundException, IOException;
    CompanyDocumentPageResponseDto getCompanyAllDocuments(EntityIdWithPageRequestDto dto);
    Boolean deleteDocument(EntityStringIdDto dto)throws ResourceNotFoundException;
    FileDownloadDto downloadFile(EntityStringIdDto dto)throws ResourceNotFoundException;
}
