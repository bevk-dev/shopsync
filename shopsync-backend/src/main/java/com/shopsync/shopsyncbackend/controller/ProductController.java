package com.shopsync.shopsyncbackend.controller;

import com.shopsync.shopsyncbackend.entity.Product;
import com.shopsync.shopsyncbackend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String category, @AuthenticationPrincipal Jwt jwt) {
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(productService.getProductsByCategory(category, jwt.getSubject()));
        }
        return ResponseEntity.ok(productService.getMyProducts(jwt.getSubject()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId, @AuthenticationPrincipal Jwt jwt) {
        Product product = productService.getProductById(productId, jwt.getSubject());
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product, @AuthenticationPrincipal Jwt jwt) {
        Product createdProduct = productService.addProduct(product, jwt.getSubject());
        return ResponseEntity.ok(createdProduct);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product productDetails, @AuthenticationPrincipal Jwt jwt) {
        Product updatedProduct = productService.updateProduct(productId, productDetails, jwt.getSubject());
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId, @AuthenticationPrincipal Jwt jwt) {
        productService.deleteProduct(productId, jwt.getSubject());
        return ResponseEntity.ok("Product " + productId + " deleted successfully.");
    }

}
