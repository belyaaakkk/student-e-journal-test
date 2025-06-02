package com.belyak.test.mapper;


import com.belyak.test.dto.FaqReadDto;
import com.belyak.test.model.Faq;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FaqMapper {
    FaqReadDto toReadDto(Faq faq);
}
