package com.fritomix.erp.modules.vehicles.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.vehicles.application.dto.request.VehicleRequest;
import com.fritomix.erp.modules.vehicles.application.dto.response.VehicleResponse;
import com.fritomix.erp.modules.vehicles.application.mapper.VehicleMapper;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import com.fritomix.erp.modules.vehicles.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper mapper;

    @Transactional(readOnly = true)
    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehicleResponse findById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + id));
        return mapper.toResponse(vehicle);
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByVehicleNumber(request.vehicleNumber())) {
            throw new IllegalArgumentException("Ya existe un vehículo con el número: " + request.vehicleNumber());
        }

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(request.vehicleNumber().toUpperCase())
                .type(request.type())
                .capacity(request.capacity())
                .dimension(request.dimension())
                .active(request.active() != null ? request.active() : true)
                .build();

        vehicle = vehicleRepository.save(vehicle);
        return mapper.toResponse(vehicle);
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con id: " + id));

        if (request.vehicleNumber() != null && !request.vehicleNumber().equalsIgnoreCase(vehicle.getVehicleNumber())) {
            if (vehicleRepository.existsByVehicleNumber(request.vehicleNumber())) {
                throw new IllegalArgumentException("Ya existe otro vehículo con el número: " + request.vehicleNumber());
            }
            vehicle.setVehicleNumber(request.vehicleNumber().toUpperCase());
        }

        if (request.type() != null) vehicle.setType(request.type());
        if (request.capacity() != null) vehicle.setCapacity(request.capacity());
        if (request.dimension() != null) vehicle.setDimension(request.dimension());
        if (request.active() != null) vehicle.setActive(request.active());

        vehicleRepository.save(vehicle);
        return mapper.toResponse(vehicle);
    }

    @Transactional
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehículo no encontrado con id: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}
