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
@Document(collation = "document_details")
public class DocumentDetails {
    @Id
    @Field(name = "id")
    private String id;
    @Field(name = "document_name")
    private String documentName;
    @Field(name = "document_type")
    private String documentType;
    @Field(name = "document_file")
    private byte[] documentFile;
    @Field(name = "content_type")
    private String contentType;
    @Field(name = "document_status")
    private Boolean documentStatus;
    @Field(name = "inserted_time")
    private Date insertedTime;
    @Field(name = "status")
    private Boolean status;
}
