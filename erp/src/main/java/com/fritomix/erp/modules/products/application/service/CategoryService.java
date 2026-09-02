package com.fritomix.erp.modules.products.application.service;

import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.products.domain.entity.Category;
import com.fritomix.erp.modules.products.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public record CategoryDTO(Long id, String name, String description, Long parentId) {}
    public record CategoryGroupDTO(Long id, String name, String description, List<CategoryDTO> children) {}
    public record CategoryCreateRequest(String name, String description, Long parentId) {}

    @Transactional(readOnly = true)
    public List<CategoryGroupDTO> findAllGroups() {
        List<Category> groups = categoryRepository.findByParentIsNullOrderByName();
        return groups.stream().map(g -> {
            List<CategoryDTO> children = categoryRepository.findByParentIdOrderByName(g.getId()).stream()
                    .map(c -> new CategoryDTO(c.getId(), c.getName(), c.getDescription(), g.getId()))
                    .toList();
            return new CategoryGroupDTO(g.getId(), g.getName(), g.getDescription(), children);
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> findChildrenByGroupId(Long groupId) {
        if (!categoryRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Grupo no encontrado con id: " + groupId);
        }
        return categoryRepository.findByParentIdOrderByName(groupId).stream()
                .map(c -> new CategoryDTO(c.getId(), c.getName(), c.getDescription(), groupId))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription(), parentId);
    }

    @Transactional
    public CategoryDTO createGroup(CategoryCreateRequest request) {
        if (categoryRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un grupo con el nombre: " + request.name());
        }
        Category group = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
        group = categoryRepository.save(group);
        return new CategoryDTO(group.getId(), group.getName(), group.getDescription(), null);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryCreateRequest request) {
        if (request.parentId() == null) {
            throw new IllegalArgumentException("Las categorías deben pertenecer a un grupo (parentId requerido)");
        }
        Category parent = categoryRepository.findById(request.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado con id: " + request.parentId()));
        if (parent.getParent() != null) {
            throw new IllegalArgumentException("El parentId debe ser un grupo (no una subcategoría)");
        }
        if (categoryRepository.existsByNameAndParentId(request.name(), request.parentId())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + request.name() + "' en este grupo");
        }
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .parent(parent)
                .build();
        category = categoryRepository.save(category);
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription(), parent.getId());
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));

        boolean isGroup = category.getParent() == null;

        if (request.name() != null && !request.name().equals(category.getName())) {
            if (isGroup) {
                if (categoryRepository.findByName(request.name()).isPresent()) {
                    throw new IllegalArgumentException("Ya existe un grupo con el nombre: " + request.name());
                }
            } else {
                Long parentId = category.getParent().getId();
                if (categoryRepository.existsByNameAndParentIdAndIdNot(request.name(), parentId, id)) {
                    throw new IllegalArgumentException("Ya existe una categoría con el nombre '" + request.name() + "' en este grupo");
                }
            }
            category.setName(request.name());
        }

        if (request.description() != null) {
            category.setDescription(request.description());
        }

        category = categoryRepository.save(category);
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription(), parentId);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        boolean isGroup = category.getParent() == null;
        if (isGroup) {
            List<Category> children = categoryRepository.findByParentIdOrderByName(id);
            if (!children.isEmpty()) {
                throw new IllegalArgumentException("No se puede eliminar el grupo '" + category.getName() + "' porque tiene " + children.size() + " categorías. Eliminá las categorías primero.");
            }
        }
        categoryRepository.deleteById(id);
    }
}
