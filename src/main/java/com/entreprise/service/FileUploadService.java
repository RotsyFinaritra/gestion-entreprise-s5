package com.entreprise.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileUploadService {
    
    @Value("${file.upload.dir:uploads}")
    private String uploadDir;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * Sauvegarde un fichier image (photo de profil)
     */
    public String saveImageFile(MultipartFile file, String candidatPrefix) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        System.out.println("FileUploadService.saveImageFile - Début");
        System.out.println("Fichier: " + file.getOriginalFilename());
        System.out.println("Taille: " + file.getSize());
        System.out.println("Type: " + file.getContentType());
        
        // Validation de la taille
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("La taille de l'image ne doit pas dépasser 10 MB");
        }
        
        // Validation du type MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image");
        }
        
        // Sauvegarder dans le dossier uploads externe
        String relativePath = saveFile(file, "images", candidatPrefix);
        System.out.println("Chemin relatif généré: " + relativePath);
        
        // Copier aussi dans le target/classes/static pour l'accès immédiat pendant le développement
        copyToStaticDir(file, relativePath);
        
        // Retourner le chemin web (accessible via /uploads/...)
        String webPath = "/uploads/" + relativePath;
        System.out.println("Chemin web retourné: " + webPath);
        return webPath;
    }
    
    /**
     * Copie le fichier dans le dossier target/classes/static pour un accès immédiat
     */
    private void copyToStaticDir(MultipartFile file, String relativePath) {
        try {
            // Copier vers target/classes/static (pour le développement)
            Path targetPath = Paths.get("target/classes/static/uploads", relativePath);
            
            // Créer les dossiers parents si nécessaire
            if (!Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }
            
            // Copier le fichier
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Fichier copié vers target/classes/static: " + targetPath);
            
            // Copier aussi vers src/main/resources/static (pour la persistance)
            Path sourcePath = Paths.get("src/main/resources/static/uploads", relativePath);
            if (!Files.exists(sourcePath.getParent())) {
                Files.createDirectories(sourcePath.getParent());
            }
            
            // Pour la copie vers src, on doit relire depuis le fichier temporaire sauvegardé
            Path uploadedPath = Paths.get(uploadDir, relativePath);
            if (Files.exists(uploadedPath)) {
                Files.copy(uploadedPath, sourcePath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Fichier copié vers src/main/resources/static: " + sourcePath);
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la copie vers static: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sauvegarde un fichier document (CV, lettre de motivation)
     */
    public String saveDocumentFile(MultipartFile file, String candidatPrefix, String type) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // Validation de la taille
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("La taille du fichier ne doit pas dépasser 5 MB");
        }
        
        // Validation du type MIME pour les documents
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && 
            !contentType.equals("application/msword") && 
            !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new IllegalArgumentException("Le fichier doit être au format PDF, DOC ou DOCX");
        }
        
        return saveFile(file, "documents", candidatPrefix + "_" + type);
    }
    
    /**
     * Méthode générique pour sauvegarder un fichier
     */
    private String saveFile(MultipartFile file, String subDir, String prefix) throws IOException {
        // Créer le répertoire de destination
        String dateDir = LocalDateTime.now().format(DATE_FORMATTER);
        Path uploadPath = Paths.get(uploadDir, subDir, dateDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Générer un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String uniqueFilename = prefix + "_" + UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFilename);
        
        // Sauvegarder le fichier
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Retourner le chemin relatif
        return subDir + "/" + dateDir + "/" + uniqueFilename;
    }
    
    /**
     * Supprime un fichier
     */
    public boolean deleteFile(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path filePath = Paths.get(uploadDir, relativePath);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Vérifie si un fichier existe
     */
    public boolean fileExists(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return false;
        }
        
        Path filePath = Paths.get(uploadDir, relativePath);
        return Files.exists(filePath);
    }
    
    /**
     * Retourne le chemin absolu d'un fichier
     */
    public Path getFilePath(String relativePath) {
        return Paths.get(uploadDir, relativePath);
    }
}
