package com.projectx.document_service.config;

import com.projectx.document_service.payloads.EntityIdDto;
import com.projectx.document_service.payloads.ResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "company-service", url = "http://localhost:1990")
public interface CompanyClient {

    @PostMapping("/companyDetails/getCompanyName")
    @CircuitBreaker(name = "company-service",fallbackMethod = "getDummyCompanyName")
    public ResponseEntity<ResponseDto<String>> getCompanyName(@Valid @RequestBody EntityIdDto dto);

    public default ResponseEntity<ResponseDto<String>> getDummyCompanyName(Throwable throwable){
        String message = "Company Service temporary down wait for sometimes!!";
        return new ResponseEntity<>(new ResponseDto<>(null,message,null), HttpStatus.OK);
    }
}
