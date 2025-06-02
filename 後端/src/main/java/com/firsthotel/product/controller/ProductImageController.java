package com.firsthotel.product.controller;

import com.firsthotel.product.util.ImageUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/product")
public class ProductImageController {

    @Autowired
    private ImageUploadUtil imageUploadUtil;

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String imagePath = imageUploadUtil.saveFile(file);
        return ResponseEntity.ok(imagePath);
    }
}
