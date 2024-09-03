package com.projectx.document_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collation = "company_documents")
public class CompanyDocuments {
    @Id
    @Field(name = "id")
    private String id;
    @Field(name = "company_id")
    private Long companyId;
    @Field(name = "document_type")
    private String documentType;
    @Field(name = "uploaded_date")
    private Date uploadedDate;
    @Field(name = "document_file")
    private byte[] documentFile;
    @Field(name = "content_type")
    private String contentType;
    @Field(name = "status")
    private Boolean status;
}
