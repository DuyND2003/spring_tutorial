package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final IgniteService igniteService;

    @Autowired
    public DataInitializer(IgniteService igniteService) {
        this.igniteService = igniteService;
    }

    @Override
    public void run(String... args) {
        // Thêm dữ liệu mẫu cho danh mục
        Category electronics = new Category(1L, "Electronics", "Electronic products");
        Category clothing = new Category(2L, "Clothing", "Clothing and accessories");
        Category books = new Category(3L, "Books", "Books and eBooks");

        igniteService.saveCategory(electronics);
        igniteService.saveCategory(clothing);
        igniteService.saveCategory(books);

        // Thêm dữ liệu mẫu cho sản phẩm
        Product laptop = new Product(1L, "Laptop", "High performance laptop", new BigDecimal("1200.00"), 1L);
        Product smartphone = new Product(2L, "Smartphone", "Latest smartphone model", new BigDecimal("800.00"), 1L);
        Product tshirt = new Product(3L, "T-Shirt", "Cotton t-shirt", new BigDecimal("25.00"), 2L);
        Product jeans = new Product(4L, "Jeans", "Blue denim jeans", new BigDecimal("45.00"), 2L);
        Product novel = new Product(5L, "Novel", "Fiction bestseller", new BigDecimal("15.00"), 3L);

        igniteService.saveProduct(laptop);
        igniteService.saveProduct(smartphone);
        igniteService.saveProduct(tshirt);
        igniteService.saveProduct(jeans);
        igniteService.saveProduct(novel);

        System.out.println("Sample data has been initialized.");
    }
}