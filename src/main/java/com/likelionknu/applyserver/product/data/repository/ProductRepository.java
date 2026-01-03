package com.likelionknu.applyserver.product.data.repository;

import com.likelionknu.applyserver.product.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
