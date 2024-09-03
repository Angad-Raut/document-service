package com.projectx.document_service.controller;

import com.projectx.document_service.exceptions.AlreadyExistsException;
import com.projectx.document_service.exceptions.ResourceNotFoundException;
import com.projectx.document_service.payloads.*;
import com.projectx.document_service.services.DocumentService;
import com.projectx.document_service.utils.ErrorHandlerComponent;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ErrorHandlerComponent errorHandler;

    @PostMapping("/addUpdateDocument")
    public ResponseEntity<ResponseDto<Boolean>> addUpdateDocument(
            @ModelAttribute @Valid DocumentDto dto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            Boolean data = documentService.addUpdateDocument(dto);
            return new ResponseEntity<>(new ResponseDto<>(
                    data,null,null), HttpStatus.CREATED);
        } catch (ResourceNotFoundException | AlreadyExistsException | IOException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(
                    null,e.getMessage(),null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getDocumentDropDown")
    public ResponseEntity<ResponseDto<List<DocumentDropDownDto>>> getDocumentDropDown() {
        try {
            List<DocumentDropDownDto> data = documentService.getDocumentDropDown();
            return new ResponseEntity<>(new ResponseDto<>(data,null,null),HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.handleError(e);
        }
    }

    @GetMapping("/getAllDocuments")
    public ResponseEntity<ResponseDto<DocumentPageResponseDto>> getAllDocuments(
            @Valid @RequestBody PageRequestDto pageRequestDto,BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            DocumentPageResponseDto data = documentService.getAllDocuments(pageRequestDto);
            return new ResponseEntity<>(new ResponseDto<>(
                    data,null,null),HttpStatus.OK);
        } catch (Exception e) {
            return errorHandler.handleError(e);
        }
    }

    @PostMapping("/getDocument")
    public ResponseEntity<ResponseDto<byte[]>> getDocument(
            @Valid @RequestBody EntityStringIdDto dto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            byte[] data = documentService.getDocument(dto);
            return new ResponseEntity<>(new ResponseDto<>(
                    data,null,null),HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(
                    null,e.getMessage(),null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/downloadDocument")
    public ResponseEntity<ResponseDto<DownloadDocDto>> downloadDocument(
            @Valid @RequestBody EntityStringIdDto dto, BindingResult result) {
        if (result.hasErrors()){
            return errorHandler.handleValidationErrors(result);
        }
        try {
            DownloadDocDto data = documentService.downloadDocument(dto);
            return new ResponseEntity<>(new ResponseDto<>(
                    data,null,null),HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return errorHandler.handleError(e);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto<>(
                    null,e.getMessage(),null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
