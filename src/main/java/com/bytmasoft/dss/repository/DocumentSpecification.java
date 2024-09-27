package com.bytmasoft.dss.repository;

import com.bytmasoft.dss.entity.Document;
import com.bytmasoft.dss.enums.DocumentType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class DocumentSpecification {

      public Specification<Document> getDocumentsByDocumentTypeAndVersion(Long ownerId, DocumentType documentType, Integer version) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if(ownerId != null && ownerId > 0) {
                predicates.add(criteriaBuilder.equal(root.get("ownerId"), ownerId));
            }

            if(documentType != null && !documentType.toString().isEmpty() ) {
                predicates.add(criteriaBuilder.equal(root.get("documentType"), documentType));
            }

            if(version != null && version > 0) {
                predicates.add(criteriaBuilder.equal(root.get("version"), version));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<Document> getDocumentsOwnerListByDocumentTypeAndVersion(List<Long> ownerIDs, DocumentType documentType, Integer version) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();


           if(ownerIDs != null && ownerIDs.size() > 0) {
               predicates.add(criteriaBuilder.and(root.get("ownerId").in(ownerIDs)));
           }

            if(documentType != null && !documentType.toString().isEmpty() ) {
                predicates.add(criteriaBuilder.equal(root.get("documentType"), documentType));
            }

            if(version != null && version > 0) {
                predicates.add(criteriaBuilder.equal(root.get("version"), version));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
