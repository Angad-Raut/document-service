package com.projectx.document_service.repository;

import com.projectx.document_service.entity.CompanyDocuments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyDocumentRepository extends MongoRepository<CompanyDocuments,String> {

    @Query(value = "{id: ?0}")
    Optional<CompanyDocuments> getById(String id);
    @Query(value = "{ $and: [{company_id: ?0 }, {status: ?1 }] }")
    Page<CompanyDocuments> getSingleCompanyDocuments(Long companyId, Boolean status, Pageable pageable);
    @Query(value = "{status: ?0}")
    Page<CompanyDocuments> getAllCompanyDocuments(Boolean status, Pageable pageable);
    @Query(value = "{id: ?0}",fields = "{document_file: 1, document_type: 1, content_type: 1}")
    List<CompanyDocuments> getFileDetails(String id);
}
