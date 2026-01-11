package com.shopsync.shopsyncbackend.service;

import com.shopsync.shopsyncbackend.entity.Product;
import com.shopsync.shopsyncbackend.entity.User;
import com.shopsync.shopsyncbackend.repository.ProductRepository;
import com.shopsync.shopsyncbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository; // Dodano
    private final AuthorizationService authService;

    public ProductService(ProductRepository productRepository, UserRepository userRepository, AuthorizationService authService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public List<Product> getMyProducts(String auth0Id) {
        return productRepository.findByOwner_Auth0Id(auth0Id);
    }

    public List<Product> getProductsByCategory(String category, String auth0Id) {
        return productRepository.findByCategoryAndOwner_Auth0Id(category, auth0Id);
    }

    public Product addProduct(Product product, String auth0Id) {
        User owner = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Uporabnik ne obstaja"));

        product.setOwner(owner);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id, String auth0Id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Izdelek ne obstaja"));

        authService.validateProductOwner(product, auth0Id);

        productRepository.delete(product);
    }

    public Product updateProduct(Long id, Product updatedProduct, String auth0Id) {
        Product existingProduct = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Izdelek z ID " + id + " ne obstaja."));

        authService.validateProductOwner(existingProduct, auth0Id);

        if (updatedProduct.getName() != null) {
            existingProduct.setName(updatedProduct.getName());
        }

        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }

        if (updatedProduct.getCategory() != null) {
            existingProduct.setCategory(updatedProduct.getCategory());
        }

        return productRepository.save(existingProduct);
    }

    public Product getProductById(Long id, String auth0Id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Izdelek ni bil najden."));

        authService.validateProductOwner(product, auth0Id);
        return product;
    }
}
