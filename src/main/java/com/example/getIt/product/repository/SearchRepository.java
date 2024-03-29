package com.example.getIt.product.repository;

import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<SearchEntity, Long> {
    SearchEntity findByKeyword(String query);
    List<SearchEntity> findByOrderByCountSearchDesc();
}
