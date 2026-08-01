package com.fritomix.erp.modules.customers.api;

import com.fritomix.erp.modules.customers.application.dto.response.CityDTO;
import com.fritomix.erp.modules.customers.application.dto.response.DepartmentDTO;
import com.fritomix.erp.modules.customers.domain.entity.City;
import com.fritomix.erp.modules.customers.domain.entity.Department;
import com.fritomix.erp.modules.customers.domain.repository.CityRepository;
import com.fritomix.erp.modules.customers.domain.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationController {

    private final DepartmentRepository departmentRepository;
    private final CityRepository cityRepository;

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO>> getDepartments() {
        List<DepartmentDTO> depts = departmentRepository.findAll().stream()
                .map(d -> new DepartmentDTO(d.getId(), d.getName()))
                .toList();
        return ResponseEntity.ok(depts);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<CityDTO>> getCitiesByDepartment(@RequestParam Long departmentId) {
        List<CityDTO> cities = cityRepository.findByDepartmentIdOrderByName(departmentId).stream()
                .map(c -> new CityDTO(c.getId(), c.getName(), c.getDepartment().getId(), c.getDepartment().getName()))
                .toList();
        return ResponseEntity.ok(cities);
    }
}
