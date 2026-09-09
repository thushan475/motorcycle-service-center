package com.thushan.motorcycleservice.service.impl;

import com.thushan.motorcycleservice.dto.request.SparePartRequestDTO;
import com.thushan.motorcycleservice.dto.response.SparePartResponseDTO;
import com.thushan.motorcycleservice.entity.SparePart;
import com.thushan.motorcycleservice.exception.DuplicateResourceException;
import com.thushan.motorcycleservice.exception.ResourceNotFoundException;
import com.thushan.motorcycleservice.repository.SparePartRepository;
import com.thushan.motorcycleservice.service.SparePartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class SparePartServiceImpl implements SparePartService {

    private final SparePartRepository sparePartRepository;
    private final ModelMapper modelMapper;

    @Override
    public SparePartResponseDTO createSparePart(SparePartRequestDTO request) {
        log.info("Execute createSparePart()");
        try {
            if (sparePartRepository.existsByPartNumber(request.getPartNumber())) {
                throw new DuplicateResourceException("A spare part with part number " + request.getPartNumber() + " already exists");
            }
            SparePart sparePart = modelMapper.map(request, SparePart.class);
            if (sparePart.getActive() == null) {
                sparePart.setActive(true);
            }
            SparePart saved = sparePartRepository.save(sparePart);
            log.info("Spare part created with id: {}", saved.getId());
            return modelMapper.map(saved, SparePartResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in createSparePart() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<SparePartResponseDTO> getAllSpareParts() {
        log.info("Execute getAllSpareParts()");
        try {
            return sparePartRepository.findAll().stream()
                    .map(sp -> modelMapper.map(sp, SparePartResponseDTO.class))
                    .toList();

        } catch (Exception e) {
            log.error("Error in getAllSpareParts() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public SparePartResponseDTO getSparePartById(Long id) {
        log.info("Execute getSparePartById()");
        try {
            return modelMapper.map(findSparePartOrThrow(id), SparePartResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in getSparePartById() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public SparePartResponseDTO updateSparePart(Long id, SparePartRequestDTO request) {
        log.info("Execute updateSparePart()");
        try {
            SparePart sparePart = findSparePartOrThrow(id);

            if (!sparePart.getPartNumber().equalsIgnoreCase(request.getPartNumber())
                    && sparePartRepository.existsByPartNumber(request.getPartNumber())) {
                throw new DuplicateResourceException("A spare part with part number " + request.getPartNumber() + " already exists");
            }

            sparePart.setPartNumber(request.getPartNumber());
            sparePart.setName(request.getName());
            sparePart.setDescription(request.getDescription());
            sparePart.setSellingPrice(request.getSellingPrice());
            if (request.getActive() != null) sparePart.setActive(request.getActive());

            SparePart updated = sparePartRepository.save(sparePart);
            log.info("Spare part updated with id: {}", updated.getId());
            return modelMapper.map(updated, SparePartResponseDTO.class);

        } catch (Exception e) {
            log.error("Error in updateSparePart() : " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteSparePart(Long id) {
        log.info("Execute deleteSparePart()");
        try {
            SparePart sparePart = findSparePartOrThrow(id);
            sparePartRepository.delete(sparePart);
            log.info("Spare part deleted with id: {}", id);

        } catch (Exception e) {
            log.error("Error in deleteSparePart() : " + e.getMessage(), e);
            throw e;
        }
    }

    private SparePart findSparePartOrThrow(Long id) {
        return sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with id: " + id));
    }
}