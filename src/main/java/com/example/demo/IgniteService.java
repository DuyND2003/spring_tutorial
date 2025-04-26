package com.example.demo;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IgniteService {

    private final Ignite ignite;
    private IgniteCache<Long, Category> categoryCache;
    private IgniteCache<Long, Product> productCache;

    @Autowired
    public IgniteService(Ignite ignite) {
        this.ignite = ignite;
    }

    @PostConstruct
    public void init() {
        // Tạo cache cho Category
        CacheConfiguration<Long, Category> categoryCacheConfig = new CacheConfiguration<>();
        categoryCacheConfig.setName("CategoryCache");
        categoryCacheConfig.setCacheMode(CacheMode.REPLICATED);
        categoryCacheConfig.setIndexedTypes(Long.class, Category.class);
        categoryCache = ignite.getOrCreateCache(categoryCacheConfig);

        // Tạo cache cho Product
        CacheConfiguration<Long, Product> productCacheConfig = new CacheConfiguration<>();
        productCacheConfig.setName("ProductCache");
        productCacheConfig.setCacheMode(CacheMode.PARTITIONED);
        productCacheConfig.setIndexedTypes(Long.class, Product.class);
        productCache = ignite.getOrCreateCache(productCacheConfig);

        // Tạo bảng SQL
        categoryCache.query(new SqlFieldsQuery(
                "CREATE TABLE IF NOT EXISTS Category (id LONG PRIMARY KEY, name VARCHAR, description VARCHAR)")).getAll();

        productCache.query(new SqlFieldsQuery(
                "CREATE TABLE IF NOT EXISTS Product (id LONG PRIMARY KEY, name VARCHAR, description VARCHAR, " +
                        "price DECIMAL, categoryId LONG)")).getAll();
    }

    // Phương thức thao tác với Category
    public void saveCategory(Category category) {
        categoryCache.put(category.getId(), category);
    }

    public Category getCategory(Long id) {
        return categoryCache.get(id);
    }

    public List<Category> getAllCategories() {
        SqlFieldsQuery query = new SqlFieldsQuery("SELECT * FROM Category");
        QueryCursor<List<?>> cursor = categoryCache.query(query);

        return cursor.getAll().stream()
                .map(row -> new Category(
                        (Long) row.get(0),
                        (String) row.get(1),
                        (String) row.get(2)
                ))
                .collect(Collectors.toList());
    }

    // Phương thức thao tác với Product
    public void saveProduct(Product product) {
        productCache.put(product.getId(), product);
    }

    public Product getProduct(Long id) {
        return productCache.get(id);
    }

    public List<Product> getAllProducts() {
        SqlFieldsQuery query = new SqlFieldsQuery("SELECT * FROM Product");
        QueryCursor<List<?>> cursor = productCache.query(query);

        return cursor.getAll().stream()
                .map(row -> new Product(
                        (Long) row.get(0),
                        (String) row.get(1),
                        (String) row.get(2),
                        (java.math.BigDecimal) row.get(3),
                        (Long) row.get(4)
                ))
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        SqlFieldsQuery query = new SqlFieldsQuery(
                "SELECT * FROM Product WHERE categoryId = ?").setArgs(categoryId);
        QueryCursor<List<?>> cursor = productCache.query(query);

        return cursor.getAll().stream()
                .map(row -> new Product(
                        (Long) row.get(0),
                        (String) row.get(1),
                        (String) row.get(2),
                        (java.math.BigDecimal) row.get(3),
                        (Long) row.get(4)
                ))
                .collect(Collectors.toList());
    }
}
