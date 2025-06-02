package com.belyak.test.service;

import com.belyak.test.dto.FaqReadDto;
import com.belyak.test.mapper.FaqMapper;
import com.belyak.test.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;
    private final FaqMapper faqMapper;

    public List<FaqReadDto> getAllFaqs() {
        return faqRepository.findAll().stream()
                .map(faqMapper::toReadDto)
                .collect(Collectors.toList());
    }
}