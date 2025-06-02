package com.firsthotel.product.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class ImageUploadUtil {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads"; // 本地上傳資料夾

    public String saveFile(MultipartFile file) {
        try {
            // 確保 uploads 資料夾存在
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 用 UUID 防止檔案名稱重複
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(UPLOAD_DIR, filename);
            file.transferTo(dest);

            // 回傳前端可以直接讀取的路徑
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("儲存圖片失敗", e);
        }
    }
}
