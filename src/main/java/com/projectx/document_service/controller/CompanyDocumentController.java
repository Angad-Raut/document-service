package com.projectx.document_service.controller;

import com.projectx.document_service.exceptions.ResourceNotFoundException;
import com.projectx.document_service.payloads.*;
import com.projectx.document_service.services.CompanyDocumentService;
import com.projectx.document_service.utils.ErrorHandlerComponent;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping(value = "/companyDocuments")
public class CompanyDocumentController {

    @Autowired
    private CompanyDocumentService companyDocumentService;

    @Autowired
    private ErrorHandlerComponent errorHandler;

    @PostMapping(value = "/getCompanyAllDocuments")
    public ResponseEntity<ResponseDto<CompanyDocumentPageResponseDto>> getCompanyAllDocuments(
            @Valid @RequestBody EntityIdWithPageRequestDto dto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            CompanyDocumentPageResponseDto data = companyDocumentService.getCompanyAllDocuments(dto);
            return new ResponseEntity<>(new ResponseDto<>(data,null,
                    null), HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.handleError(e);
        }
    }

    @PostMapping(value = "/addDocumentByCompanyId",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ResponseDto<Boolean>> addDocumentByCompanyId(
            @ModelAttribute @Valid CompanyDocDto dto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            Boolean data = companyDocumentService.addDocumentByCompanyId(dto);
            return new ResponseEntity<>(new ResponseDto<>(data,null,
                    null),HttpStatus.OK);
        } catch (ResourceNotFoundException | IOException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(null,e.getMessage(),
                    null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/deleteDocumentById")
    public ResponseEntity<ResponseDto<Boolean>> deleteDocumentById(
            @RequestBody @Valid EntityStringIdDto dto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            Boolean data = companyDocumentService.deleteDocument(dto);
            return new ResponseEntity<>(new ResponseDto<>(data,null,
                    null),HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(null,e.getMessage(),
                    null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/downloadDocumentFile")
    public ResponseEntity<ResponseDto<FileDownloadDto>> downloadDocumentFile(
            @RequestBody @Valid EntityStringIdDto dto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            FileDownloadDto data = companyDocumentService.downloadFile(dto);
            return new ResponseEntity<>(new ResponseDto<>(data,null,
                    null),HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(null,e.getMessage(),
                    null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
