package vn.tuannpa.coursefpt.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;

@Service
public class UploadService {
    private final ServletContext servletContext;

    public UploadService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String handSaveUpLoadFile(MultipartFile file, String targetFolder) {
        if(file.isEmpty()) {
            return ""; //don't upload file
        }

        //relative path: absolute path
        String rootPath = this.servletContext.getRealPath("/resources/images");
        String finalName = "";
        try {
            byte[] bytes = file.getBytes();
            File dir = new File(rootPath + File.separator + targetFolder);
            if(!dir.exists()) {
                dir.mkdir();
                
                //Create the file on server
                finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

                File serverFile = new File(dir.getAbsolutePath() + File.separator + finalName);
                //uuid

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return finalName;
    }
}
