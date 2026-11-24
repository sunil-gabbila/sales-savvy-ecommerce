package com.kodnest.salessavvy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kodnest.salessavvy.entity.Category;
import com.kodnest.salessavvy.entity.Product;
import com.kodnest.salessavvy.entity.ProductImage;
import com.kodnest.salessavvy.repository.CategoryRepository;
import com.kodnest.salessavvy.repository.ProductImageRepository;
import com.kodnest.salessavvy.repository.ProductRepository;
@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final CategoryRepository categoryRepository;

    ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getProductsByCategory(String categoryName) {
        if (categoryName != null && !categoryName.isEmpty()) {
            Optional<Category> categoryOpt = categoryRepository.findByCategoryName(categoryName);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                return productRepository.findByCategory_CategoryId(category.getCategoryId());
            } else {
                throw new RuntimeException("Category not found");
            }
        } else {
            return productRepository.findAll();
        }
    }

    public List<String> getProductImages(Integer productId) {
        List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(productId);
        List<String> imageUrls = new ArrayList<>();
        for (ProductImage image : productImages) {
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }
}