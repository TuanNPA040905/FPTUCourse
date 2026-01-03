package vn.tuannpa.coursefpt.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {
    
    @Value("${upload.path:src/main/resources/static/images}")   // inject giá trị từ application.properties
    private String uploadPath;

    public String handSaveUpLoadFile(MultipartFile file, String targetFolder) {
        if(file.isEmpty()) {
            System.out.println("File is empty!");
            return "";
        }

        try {
            // Tạo đường dẫn: src/main/resources/static/images/avatar/
            Path uploadDir = Paths.get(uploadPath, targetFolder);
            // Paths.get() tự động xử lý dấu / hoặc \ tuỳ hệ điều hành
            
            System.out.println("📂 Upload path: " + uploadPath);
            System.out.println("📁 Full directory: " + uploadDir.toAbsolutePath());
            
            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                System.out.println("✅ Created directory: " + uploadDir.toAbsolutePath());
            }

            // Tạo tên file unique
            String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            
            // Đường dẫn file đầy đủ
            Path filePath = uploadDir.resolve(finalName);
            
            // Lưu file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("✅ File saved: " + filePath.toAbsolutePath());
            System.out.println("🔗 Access URL: /images/" + targetFolder + "/" + finalName);
            
            return finalName;
            
        } catch(IOException e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}