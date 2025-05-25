package com.textscanner.analysis.repository;

import com.textscanner.analysis.model.TextAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TextAnalysisRepository extends JpaRepository<TextAnalysis, Long> {
    List<TextAnalysis> findByFileId(Long fileId);
} 