package com.projectx.document_service.services;

import com.projectx.document_service.config.CompanyClient;
import com.projectx.document_service.entity.CompanyDocuments;
import com.projectx.document_service.exceptions.ResourceNotFoundException;
import com.projectx.document_service.payloads.*;
import com.projectx.document_service.repository.CompanyDocumentRepository;
import com.projectx.document_service.utils.DocumentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CompanyDocumentServiceImpl implements CompanyDocumentService {

    @Autowired
    private CompanyDocumentRepository companyDocumentRepository;

    @Autowired
    private CompanyClient companyClient;

    @Override
    public Boolean addDocumentByCompanyId(CompanyDocDto dto)
            throws ResourceNotFoundException, IOException {
        try {
            ResponseEntity<ResponseDto<String>> response = companyClient.getCompanyName(new EntityIdDto(dto.getCompanyId()));
            if (response.getBody()!=null && response.getBody().getResult()==null
                && response.getBody().getErrorMessage()!=null) {
                throw new ResourceNotFoundException(response.getBody().getErrorMessage());
            }
            CompanyDocuments companyDocuments = CompanyDocuments.builder()
                    .documentType(dto.getDocumentType())
                    .uploadedDate(new Date())
                    .documentFile(dto.getDocumentFile().getBytes())
                    .contentType(dto.getDocumentFile().getContentType())
                    .companyId(dto.getCompanyId())
                    .build();
            return companyDocumentRepository.save(companyDocuments)!=null?true:false;
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public CompanyDocumentPageResponseDto getCompanyAllDocuments(EntityIdWithPageRequestDto dto) {
        String sortParameter = "";
        if (dto.getSortParam()!=null && dto.getSortParam().equals("srNo")) {
            sortParameter = "id";
        } else if (dto.getSortParam()!=null && dto.getSortParam().equals("documentType")) {
            sortParameter = "document_type";
        } else if (dto.getSortParam()!=null && dto.getSortParam().equals("contentType")) {
            sortParameter = "content_type";
        } else if (dto.getSortParam()!=null && dto.getSortParam().equals("uploadedDate")) {
            sortParameter = "uploaded_date";
        } else {
            sortParameter = "uploaded_date";
        }
        Sort sort = dto.getSortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortParameter).ascending()
                : Sort.by(sortParameter).descending();
        Pageable pageable = PageRequest.of(dto.getPageNumber()-1, dto.getPageSize(), sort);
        Page<CompanyDocuments> documents = null;
        if (dto.getEntityId()!=null) {
            documents = companyDocumentRepository.getSingleCompanyDocuments(dto.getEntityId(),true, pageable);
        } else {
            documents = companyDocumentRepository.getAllCompanyDocuments(true,pageable);
        }
        Integer pageNumber = dto.getPageNumber()-1;
        AtomicInteger index = new AtomicInteger(dto.getPageSize()*pageNumber);
        List<CompanyDocuments> listOfDocuments = documents.getContent();
        List<ViewCompanyDocumentDto> documentList = !listOfDocuments.isEmpty()?listOfDocuments.stream()
                .map(data -> ViewCompanyDocumentDto.builder()
                        .srNo(index.incrementAndGet())
                        .documentId(data.getId()!=null?data.getId():null)
                        .customerId(data.getCompanyId()!=null?data.getCompanyId():null)
                        .companyName(data.getCompanyId()!=null?setCompanyName(data.getCompanyId()):DocumentUtils.DASH)
                        .documentType(data.getDocumentType()!=null?setDocumentType(data.getDocumentType()):DocumentUtils.DASH)
                        .uploadedDate(data.getUploadedDate()!=null?DocumentUtils.toExpenseDate(data.getUploadedDate()):DocumentUtils.DASH)
                        .contentType(data.getContentType()!=null?data.getContentType():DocumentUtils.DASH)
                        .build()).toList()
                :new ArrayList<>();
        return !documentList.isEmpty()?CompanyDocumentPageResponseDto.builder()
                .pageNo(documents.getNumber())
                .pageSize(documents.getSize())
                .totalPages(documents.getTotalPages())
                .totalElements(documents.getTotalElements())
                .content(documentList)
                .build():new CompanyDocumentPageResponseDto();
    }

    @Override
    public Boolean deleteDocument(EntityStringIdDto dto) throws ResourceNotFoundException {
        try {
            CompanyDocuments documents = companyDocumentRepository.getById(dto.getEntityId()).get();
            if (documents==null) {
                throw new ResourceNotFoundException(DocumentUtils.COMPANY_DOCUMENT_NOT_EXISTS);
            }
            companyDocumentRepository.delete(documents);
            return true;
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public FileDownloadDto downloadFile(EntityStringIdDto dto) throws ResourceNotFoundException {
        try {
            List<CompanyDocuments> fetchData = companyDocumentRepository.getFileDetails(dto.getEntityId());
            if (fetchData==null && fetchData.isEmpty()) {
                throw new ResourceNotFoundException(DocumentUtils.COMPANY_DOCUMENT_NOT_EXISTS);
            }
            if (fetchData.get(0).getDocumentFile()==null && fetchData.get(0).getDocumentType()==null
                && fetchData.get(0).getContentType()==null) {
                throw new ResourceNotFoundException(DocumentUtils.COMPANY_DOCUMENT_NOT_EXISTS);
            }
            CompanyDocuments data = fetchData.get(0);
            return FileDownloadDto.builder()
                    .documentType(data.getDocumentType()!=null?data.getDocumentType():DocumentUtils.DASH)
                    .contentType(data.getContentType()!=null?data.getContentType():DocumentUtils.DASH)
                    .documentFile(data.getDocumentFile()!=null?data.getDocumentFile():null)
                    .build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    private String setDocumentType(String type) {
        if (type.equals(DocumentUtils.SALARY_TYPE)) {
            return "Salary Slip";
        } else if (type.equals(DocumentUtils.FORM_16_TYPE)) {
            return "Form 16";
        } else if (type.equals(DocumentUtils.OFFER_LETTER_TYPE)) {
            return "Offer Letter";
        } else if (type.equals(DocumentUtils.EXPERIENCE_LETTER_TYPE)) {
            return "Experience Letter";
        } else if (type.equals(DocumentUtils.SERVICE_LETTER_TYPE)) {
            return "Service Letter";
        } else if (type.equals(DocumentUtils.APPOINTMENT_LETTER_TYPE)) {
            return "Appointment Letter";
        } else {
            return DocumentUtils.DASH;
        }
    }
    private String setCompanyName(Long companyId){
        ResponseEntity<ResponseDto<String>> response = companyClient.getCompanyName(new EntityIdDto(companyId));
        if (response.getBody()!=null && response.getBody().getResult()!=null
            && response.getBody().getErrorMessage()==null){
            return response.getBody().getResult();
        } else {
            return DocumentUtils.DASH;
        }
    }
}
