package com.belyak.test.mapper;

import com.belyak.test.dto.ParentReadDto;
import com.belyak.test.model.Parent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, StudentMapper.class})
public interface ParentMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "children", target = "children")
    ParentReadDto toReadParentDto(Parent parent);
}