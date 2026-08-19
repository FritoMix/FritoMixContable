package com.fritomix.erp.modules.drivers.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.drivers.application.dto.request.DriverRequest;
import com.fritomix.erp.modules.drivers.application.dto.response.DriverResponse;
import com.fritomix.erp.modules.drivers.application.mapper.DriverMapper;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.drivers.domain.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<DriverResponse> findAll(String search, Pageable pageable) {
        String term = StringUtils.hasText(search) ? "%" + search.trim() + "%" : null;
        return PageResponse.from(driverRepository.search(term, pageable), mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DriverResponse findById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + id));
        return mapper.toResponse(driver);
    }

    @Transactional
    public DriverResponse create(DriverRequest request) {
        if (driverRepository.existsByDocument(request.document())) {
            throw new IllegalArgumentException("Ya existe un conductor con el documento: " + request.document());
        }

        Driver driver = Driver.builder()
                .document(request.document())
                .name(request.name())
                .phone(request.phone())
                .licenseNumber(request.licenseNumber())
                .active(request.active() != null ? request.active() : true)
                .build();

        driver = driverRepository.save(driver);
        return mapper.toResponse(driver);
    }

    @Transactional
    public DriverResponse update(Long id, DriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con id: " + id));

        if (request.document() != null && !request.document().equals(driver.getDocument())) {
            if (driverRepository.existsByDocument(request.document())) {
                throw new IllegalArgumentException("Ya existe otro conductor con el documento: " + request.document());
            }
            driver.setDocument(request.document());
        }

        if (request.name() != null) driver.setName(request.name());
        if (request.phone() != null) driver.setPhone(request.phone());
        if (request.licenseNumber() != null) driver.setLicenseNumber(request.licenseNumber());
        if (request.active() != null) driver.setActive(request.active());

        driverRepository.save(driver);
        return mapper.toResponse(driver);
    }

    @Transactional
    public void delete(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conductor no encontrado con id: " + id);
        }
        driverRepository.deleteById(id);
    }
}
