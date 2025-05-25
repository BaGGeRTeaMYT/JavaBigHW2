package com.textscanner.filestore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class StoredFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String filename;
    
    @Lob
    private byte[] content;
    
    private String contentType;
    
    private LocalDateTime uploadDate;
    
    private String hash; // For plagiarism detection
} 