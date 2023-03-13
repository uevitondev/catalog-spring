package com.uevitondev.catalog.services;

import com.uevitondev.catalog.dto.ProductDTO;
import com.uevitondev.catalog.entities.Product;
import com.uevitondev.catalog.repository.ProductRepository;
import com.uevitondev.catalog.services.exceptions.DatabaseException;
import com.uevitondev.catalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllProductsPaged(PageRequest pageRequest) {
        Page<Product> categoryPage = productRepository.findAll(pageRequest);
        return categoryPage.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found! id: " + id));
        return new ProductDTO(product, product.getCategories());
    }

    @Transactional
    public ProductDTO insertProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        try {
            Product product = productRepository.getReferenceById(id);
            product.setName(productDTO.getName());
            product = productRepository.save(product);
            return new ProductDTO(product);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found! id: " + id);
        }
    }

    public void deleteProduct(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found! id: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation!");
        }
    }

}
