package com.likelionknu.applyserver.product.controller;

import com.likelionknu.applyserver.common.response.GlobalResponse;
import com.likelionknu.applyserver.product.data.dto.request.ProductRequestDto;
import com.likelionknu.applyserver.product.data.dto.response.ProductResponseDto;
import com.likelionknu.applyserver.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public GlobalResponse<ProductResponseDto> getProduct(@PathVariable Long id) {
        return GlobalResponse.ok(productService.getProduct(id));
    }

    @PostMapping("/")
    public GlobalResponse<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        return GlobalResponse.ok(productService.saveProduct(productRequestDto));
    }

    @DeleteMapping("/{id}")
    public GlobalResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return GlobalResponse.ok();
    }

    @PutMapping("/{id}")
    public GlobalResponse<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDto productRequestDto) {
        return GlobalResponse.ok(productService.updateProduct(id, productRequestDto));
    }
}
