package com.projectx.document_service.repository;

import com.projectx.document_service.entity.DocumentDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentDetailsRepository extends MongoRepository<DocumentDetails,String> {
    @Query("{id :?0}")
    Optional<DocumentDetails> getById(String id);

    @Query(value = "{id: ?0}",fields = "{document_file : 1}")
    List<DocumentDetails> getDocumentFile(String id);

    @Query(value = "{status: ?0}", count = true)
    public Long documentCount(Boolean status);

    @Query(value = "{document_name: ?0}", exists = true)
    Boolean existsDocumentDetailsByDocumentName(String document);

    @Query(value = "{status: ?0}")
    Page<DocumentDetails> getAllDocuments(Boolean status, Pageable pageable);
}
