package com.bytmasoft.dss.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DocumentCreateDTO extends DocumentUpdateDTO {
    private String fileName;
}
