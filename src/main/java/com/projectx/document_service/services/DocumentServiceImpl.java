package com.projectx.document_service.services;

import com.projectx.document_service.entity.DocumentDetails;
import com.projectx.document_service.exceptions.AlreadyExistsException;
import com.projectx.document_service.exceptions.ResourceNotFoundException;
import com.projectx.document_service.payloads.*;
import com.projectx.document_service.repository.DocumentDetailsRepository;
import com.projectx.document_service.utils.DocumentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DocumentServiceImpl implements DocumentService{

    @Autowired
    private DocumentDetailsRepository documentRepository;

    @Override
    public Boolean addUpdateDocument(DocumentDto dto) throws ResourceNotFoundException,
            AlreadyExistsException, IOException {
         DocumentDetails documentDetails = null;
         if (dto.getId()==null) {
                  isDocumentExist(dto.getDocumentName());
                  documentDetails = DocumentDetails.builder()
                          .documentName(dto.getDocumentName())
                          .documentType(dto.getDocumentType())
                          .documentFile(dto.getDocumentFile().getBytes())
                          .documentStatus(true)
                          .insertedTime(new Date())
                          .build();
         } else {
             documentDetails = documentRepository.getById(dto.getId()).get();
             if (documentDetails == null) {
                 throw new ResourceNotFoundException(DocumentUtils.DOCUMENT_NOT_FOUND);
             }
             if (!dto.getDocumentName().equals(documentDetails.getDocumentName())) {
                 isDocumentExist(dto.getDocumentName());
                 documentDetails.setDocumentName(dto.getDocumentName());
             }
             if (!dto.getDocumentType().equals(documentDetails.getDocumentType())) {
                 documentDetails.setDocumentType(dto.getDocumentType());
             }
             if (!dto.getDocumentFile().getBytes().equals(documentDetails.getDocumentFile())) {
                 documentDetails.setDocumentFile(dto.getDocumentFile().getBytes());
             }
         }
         try {
            return documentRepository.save(documentDetails)!=null?true:false;
         } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
         } catch (AlreadyExistsException e) {
            throw new AlreadyExistsException(e.getMessage());
         }
    }

    @Override
    public DocumentPageResponseDto getAllDocuments(PageRequestDto dto) {
        String sortParameter = "";
        if (dto.getSortParam()!=null && dto.getSortParam().equals("srNo")) {
            sortParameter = "id";
        } else if (dto.getSortParam()!=null && dto.getSortParam().equals("documentType")) {
            sortParameter = "document_type";
        } else if (dto.getSortParam()!=null && dto.getSortParam().equals("documentName")) {
            sortParameter = "document_name";
        } else if (dto.getSortParam()!=null && dto.getSortParam().equals("uploadedDate")) {
            sortParameter = "inserted_time";
        } else {
            sortParameter = "inserted_time";
        }
        Sort sort = dto.getSortDir().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortParameter).ascending()
                : Sort.by(sortParameter).descending();
        Pageable pageable = PageRequest.of(dto.getPageNumber()-1, dto.getPageSize(), sort);
        Page<DocumentDetails> documents = documentRepository.getAllDocuments(true,pageable);
        Integer pageNumber = dto.getPageNumber()-1;
        AtomicInteger index = new AtomicInteger(dto.getPageSize()*pageNumber);
        List<DocumentDetails> listOfDocuments = documents.getContent();
        List<ViewDocumentsDto> documentList = !listOfDocuments.isEmpty()?listOfDocuments.stream()
                .map(data -> ViewDocumentsDto.builder()
                        .srNo(index.incrementAndGet())
                        .documentId(data.getId()!=null?data.getId():null)
                        .documentType(data.getDocumentType()!=null?data.getDocumentType():DocumentUtils.DASH)
                        .uploadedDate(data.getInsertedTime()!=null?DocumentUtils.toExpenseDate(data.getInsertedTime()):DocumentUtils.DASH)
                        .documentName(data.getDocumentName()!=null?data.getDocumentName():DocumentUtils.DASH)
                        .build()).toList()
                :new ArrayList<>();
        return !documentList.isEmpty()?DocumentPageResponseDto.builder()
                .pageNo(documents.getNumber())
                .pageSize(documents.getSize())
                .totalPages(documents.getTotalPages())
                .totalElements(documents.getTotalElements())
                .content(documentList)
                .build():new DocumentPageResponseDto();
    }

    @Override
    public List<DocumentDropDownDto> getDocumentDropDown() {
        List<DocumentDropDownDto> response = new ArrayList<>();
        response.add(new DocumentDropDownDto("PERSONAL","Personal"));
        response.add(new DocumentDropDownDto("EDUCATIONAL","Educational"));
        response.add(new DocumentDropDownDto("PROFESSIONAL","Professional"));
        return response;
    }

    @Override
    public byte[] getDocument(EntityStringIdDto dto) throws ResourceNotFoundException {
        try {
            List<DocumentDetails> data = documentRepository.getDocumentFile(dto.getEntityId());
            if (data==null && data.get(0).getDocumentFile()==null) {
                throw new ResourceNotFoundException(DocumentUtils.DOCUMENT_NOT_FOUND);
            }
            return data.get(0).getDocumentFile();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public DownloadDocDto downloadDocument(EntityStringIdDto dto) throws ResourceNotFoundException {
        try {
            DocumentDetails data = documentRepository.getById(dto.getEntityId()).get();
            if (data==null) {
                throw new ResourceNotFoundException(DocumentUtils.DOCUMENT_NOT_FOUND);
            }
            return DownloadDocDto.builder()
                    .documentName(data.getDocumentName())
                    .documentFile(data.getDocumentFile())
                    .build();
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Override
    public Integer documentCount() {
        Long count = documentRepository.documentCount(true);
        Integer documentCount = Math.toIntExact(count);
        return documentCount!=null?documentCount:0;
    }

    private void isDocumentExist(String document) {
        if (documentRepository.existsDocumentDetailsByDocumentName(document)) {
            throw new AlreadyExistsException(DocumentUtils.DOCUMENT_ALREADY_EXIST);
        }
    }
}
