package com.bytmasoft.dss.mapper;

import com.bytmasoft.dss.dto.DocumentDto;
import com.bytmasoft.dss.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {

    DocumentDto documentToDto(Document document);

}
