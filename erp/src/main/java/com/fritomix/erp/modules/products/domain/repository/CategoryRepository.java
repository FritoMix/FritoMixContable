package com.fritomix.erp.modules.products.domain.repository;

import com.fritomix.erp.modules.products.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
