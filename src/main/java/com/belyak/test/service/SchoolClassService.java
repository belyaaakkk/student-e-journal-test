package com.belyak.test.service;

import com.belyak.test.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;

    public List<String> getAllClassNames() {
        return schoolClassRepository.findAllClassCodes();
    }
}
