package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final IgniteService igniteService;

    @Autowired
    public CategoryController(IgniteService igniteService) {
        this.igniteService = igniteService;
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody Category category) {
        igniteService.saveCategory(category);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Category category = igniteService.getCategory(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = igniteService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
