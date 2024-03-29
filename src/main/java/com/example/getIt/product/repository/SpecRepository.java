package com.example.getIt.product.repository;

import com.example.getIt.product.entity.SpecEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecRepository extends JpaRepository<SpecEntity, String> {
    List<SpecEntity> findAllByTypeAndForuseAndForprice(String type, String foruse, String forprice);
    List<SpecEntity> findAllByTypeAndForuseAndForpriceAndPlus(String type, String foruse, String forprice, String plus);
}
