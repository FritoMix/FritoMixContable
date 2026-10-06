package com.fritomix.erp.modules.products.domain.repository;

import com.fritomix.erp.modules.products.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    List<Category> findByParentIsNullOrderByName();
    List<Category> findByParentIdOrderByName(Long parentId);
    boolean existsByNameAndParentId(String name, Long parentId);
    boolean existsByNameAndParentIdAndIdNot(String name, Long parentId, Long id);

    @Query("""
        SELECT COUNT(p) FROM Product p
        WHERE p.category.id = :catId
           OR p.category.parent.id = :catId
           OR p.category.parent.parent.id = :catId
    """)
    long countProductsByCategoryIdRecursive(@Param("catId") Long catId);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.parent.id = :catId")
    long countSubcategoriesByParentId(@Param("catId") Long catId);
}
