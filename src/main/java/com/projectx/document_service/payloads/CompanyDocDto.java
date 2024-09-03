package com.projectx.document_service.payloads;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDocDto {
    @NotNull(message = "Please select company!!")
    private Long companyId;
    @NotNull(message = "Please select document type!!")
    private String documentType;
    @NotNull(message = "Please select document file!!")
    private MultipartFile documentFile;
}
