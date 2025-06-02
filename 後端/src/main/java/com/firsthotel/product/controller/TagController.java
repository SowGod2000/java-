package com.firsthotel.product.controller;

import com.firsthotel.product.model.Tag;
import com.firsthotel.product.repository.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // ✅ 取得所有標籤
    @GetMapping
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    // ✅ 新增標籤
    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody Tag tag) {
        if (tagRepository.existsByName(tag.getName())) {
            return ResponseEntity.badRequest().body("標籤名稱已存在");
        }
        return ResponseEntity.ok(tagRepository.save(tag));
    }
 // ✅ 查詢單一標籤 by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTagById(@PathVariable Long id) {
        return tagRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // ✅ 更新標籤
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTag(@PathVariable Long id, @RequestBody Tag updated) {
        return tagRepository.findById(id).map(tag -> {
            tag.setName(updated.getName());
            return ResponseEntity.ok(tagRepository.save(tag));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ 刪除標籤
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        tagRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
