package com.benimsin.recipemanagementservice.service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinaryConfig;

    public Map<String, String> uploadFile(MultipartFile file) {
        try {
            File uploadedFile = convertMultiPartToFile(file);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, Collections.emptyMap());
            uploadedFile.delete();
            Map<String, String> map = new HashMap<>();
            map.put("url", uploadResult.get("url").toString());
            map.put("publicId", uploadResult.get("public_id").toString());
            return  map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public boolean deleteFile(String publicId){
        try {
            Map deleteResult = cloudinaryConfig.uploader().destroy(publicId, Collections.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}
