package com.erp.service;

import com.erp.dto.PositionDto;
import com.erp.entity.Position;
import com.erp.exception.EntityNotFoundException;
import com.erp.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionService {

    private final PositionRepository positionRepository;

    public Page<PositionDto.Response> getAllPositions(Pageable pageable) {
        return positionRepository.findAll(pageable)
                .map(PositionDto.Response::from);
    }

    public List<PositionDto.Response> getAllPositionsList() {
        return positionRepository.findAll().stream()
                .map(PositionDto.Response::from)
                .collect(Collectors.toList());
    }

    public PositionDto.Response getPositionById(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position", id.toString()));
        return PositionDto.Response.from(position);
    }

    @Transactional
    public PositionDto.Response createPosition(PositionDto.Request request) {
        Position position = Position.builder()
                .positionName(request.getPositionName())
                .positionLevel(request.getPositionLevel())
                .build();

        Position saved = positionRepository.save(position);
        return PositionDto.Response.from(saved);
    }

    @Transactional
    public PositionDto.Response updatePosition(Long id, PositionDto.UpdateRequest request) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position", id.toString()));

        positionRepository.delete(position);
        
        Position updated = Position.builder()
                .id(id)
                .positionName(request.getPositionName())
                .positionLevel(request.getPositionLevel())
                .build();

        Position saved = positionRepository.save(updated);
        return PositionDto.Response.from(saved);
    }

    @Transactional
    public void deletePosition(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new EntityNotFoundException("Position", id.toString());
        }
        positionRepository.deleteById(id);
    }
}
