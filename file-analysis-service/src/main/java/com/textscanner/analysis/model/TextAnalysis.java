package com.textscanner.analysis.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class TextAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long fileId;
    private int paragraphCount;
    private int wordCount;
    private int characterCount;
    private String fileHash;
    private boolean hasPlagiarism;
    
    @ElementCollection
    @CollectionTable(name = "plagiarism_matches", joinColumns = @JoinColumn(name = "analysis_id"))
    @Column(name = "file_id")
    private List<Long> plagiarismMatchFileIds;
} 