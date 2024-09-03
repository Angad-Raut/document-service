package com.projectx.document_service.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageRequestDto {
    private Integer pageNumber;
    private Integer pageSize;
    private String sortParam;
    private String sortDir;
}
