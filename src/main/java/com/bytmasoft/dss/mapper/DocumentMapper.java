package com.bytmasoft.dss.mapper;

import com.bytmasoft.dss.dto.DocumentDTO;
import com.bytmasoft.dss.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {

    Document dtoToEntity(DocumentDTO var1);

    // @Mapping(target = "roles", source = "roles", qualifiedByName = "roleSet")
    DocumentDTO entityToDto(Document var1);



}
