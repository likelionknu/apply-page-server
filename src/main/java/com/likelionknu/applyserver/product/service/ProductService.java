package com.likelionknu.applyserver.product.service;

import com.likelionknu.applyserver.product.data.dto.request.ProductRequestDto;
import com.likelionknu.applyserver.product.data.dto.response.ProductResponseDto;
import com.likelionknu.applyserver.product.data.entity.Product;
import com.likelionknu.applyserver.product.data.repository.ProductRepository;
import com.likelionknu.applyserver.product.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
    }

    public ProductResponseDto getProduct(Long id) {
        Product product = getProductEntity(id);

        return ProductResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                    .build();
    }

    public ProductResponseDto saveProduct(ProductRequestDto productRequestDto) {
        Product product = Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
            .build();

        productRepository.save(product);

        return ProductResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                    .build();
    }

    public void deleteProduct(Long id) {
        Product product = getProductEntity(id);
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductRequestDto productRequestDto) {
        Product product = getProductEntity(productId);
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());

        return ProductResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                    .build();
    }
}
