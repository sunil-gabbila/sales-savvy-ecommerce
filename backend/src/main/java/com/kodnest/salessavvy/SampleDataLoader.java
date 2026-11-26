package com.kodnest.salessavvy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.kodnest.salessavvy.entity.Category;
import com.kodnest.salessavvy.entity.Product;
import com.kodnest.salessavvy.entity.ProductImage;
import com.kodnest.salessavvy.entity.Role;
import com.kodnest.salessavvy.entity.User;
import com.kodnest.salessavvy.repository.CategoryRepository;
import com.kodnest.salessavvy.repository.ProductImageRepository;
import com.kodnest.salessavvy.repository.ProductRepository;
import com.kodnest.salessavvy.repository.UserRepository;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SampleDataLoader(UserRepository userRepository,
                             CategoryRepository categoryRepository,
                             ProductRepository productRepository,
                             ProductImageRepository productImageRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        userRepository.save(new User("admin", "admin@salessavvy.local", passwordEncoder.encode("admin123"), Role.ADMIN, now, now));
        userRepository.save(new User("sunil", "sunil@salessavvy.local", passwordEncoder.encode("customer123"), Role.CUSTOMER, now, now));

        Category shirts = categoryRepository.save(new Category("Shirts"));
        Category pants = categoryRepository.save(new Category("Pants"));
        Category accessories = categoryRepository.save(new Category("Accessories"));
        Category mobiles = categoryRepository.save(new Category("Mobiles"));
        Category mobileAccessories = categoryRepository.save(new Category("Mobile Accessories"));

        saveProduct("Oxford Cotton Shirt", "Light blue cotton shirt, regular fit.", "1299.00", 40, shirts,
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=600&q=80");
        saveProduct("Linen Casual Shirt", "Beige linen shirt for everyday wear.", "999.00", 35, shirts,
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=600&q=80");
        saveProduct("Slim Fit Chinos", "Navy chinos with a tapered leg.", "1499.00", 28, pants,
                "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=600&q=80");
        saveProduct("Classic Denim Jeans", "Mid-wash straight jeans.", "1799.00", 32, pants,
                "https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=600&q=80");
        saveProduct("Leather Belt", "Brown full-grain leather belt.", "699.00", 50, accessories,
                "https://images.unsplash.com/photo-1624222247344-550fb60583c2?auto=format&fit=crop&w=600&q=80");
        saveProduct("Analog Wrist Watch", "Steel case with a leather strap.", "2499.00", 18, accessories,
                "https://images.unsplash.com/photo-1524805444758-089113d48a6d?auto=format&fit=crop&w=600&q=80");
        saveProduct("Nova Phone", "6.5 inch display, 128 GB storage.", "18999.00", 15, mobiles,
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=600&q=80");
        saveProduct("Pixel Mini", "Compact phone with a 50 MP camera.", "12999.00", 20, mobiles,
                "https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=600&q=80");
        saveProduct("USB-C Cable", "1 metre braided charging cable.", "299.00", 80, mobileAccessories,
                "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?auto=format&fit=crop&w=600&q=80");
        saveProduct("Wireless Earbuds", "In-ear buds with a charging case.", "1999.00", 25, mobileAccessories,
                "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=600&q=80");
    }

    private void saveProduct(String name, String description, String price, int stock, Category category, String imageUrl) {
        LocalDateTime now = LocalDateTime.now();
        Product product = productRepository.save(new Product(name, description, new BigDecimal(price), stock, category, now, now));
        productImageRepository.save(new ProductImage(product, imageUrl));
    }
}
