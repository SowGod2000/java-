package com.firsthotel.product.controller;

import com.firsthotel.product.model.Category;
import com.firsthotel.product.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ✅ 取得所有分類
    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    // ✅ 新增分類
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Category category) {
        String name = category.getName() != null ? category.getName().trim() : "";
        if (name.isEmpty()) {
            return ResponseEntity.badRequest().body("分類名稱不可為空白");
        }
        if (categoryRepository.existsByName(name)) {
            return ResponseEntity.badRequest().body("分類名稱已存在");
        }
        category.setName(name);
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    // ✅ 更新分類
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Category updated) {
        String name = updated.getName() != null ? updated.getName().trim() : "";
        if (name.isEmpty()) {
            return ResponseEntity.badRequest().body("分類名稱不可為空白");
        }

        return categoryRepository.findById(id).map(category -> {
            category.setName(name);
            return ResponseEntity.ok(categoryRepository.save(category));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ 刪除分類
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
