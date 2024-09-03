package com.projectx.document_service.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDto {
    private String id;
    private String documentName;
    private String documentType;
    private MultipartFile documentFile;
}
