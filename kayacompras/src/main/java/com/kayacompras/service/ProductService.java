package com.kayacompras.service;

import com.kayacompras.model.Product;
import com.kayacompras.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findByNameContaining(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}
