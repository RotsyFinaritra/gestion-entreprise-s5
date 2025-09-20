package com.entreprise.controller;

import com.entreprise.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Path;

@Controller
@RequestMapping("/files")
public class FileController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @GetMapping("/images/{date}/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String date, @PathVariable String filename) {
        try {
            String relativePath = "images/" + date + "/" + filename;
            Path filePath = fileUploadService.getFilePath(relativePath);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Par défaut, on peut aussi détecter le type
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/documents/{date}/{filename:.+}")
    public ResponseEntity<Resource> getDocument(@PathVariable String date, @PathVariable String filename) {
        try {
            String relativePath = "documents/" + date + "/" + filename;
            Path filePath = fileUploadService.getFilePath(relativePath);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
