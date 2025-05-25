package com.textscanner.analysis.controller;

import com.textscanner.analysis.model.TextAnalysis;
import com.textscanner.analysis.service.TextAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import feign.FeignException;
import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);

    @Autowired
    private TextAnalysisService analysisService;

    @PostMapping("/analyze/{fileId}")
    public ResponseEntity<?> analyzeFile(@PathVariable Long fileId) {
        try {
            logger.info("Starting analysis for file ID: {}", fileId);
            TextAnalysis analysis = analysisService.analyzeFile(fileId);
            logger.info("Analysis completed for file ID: {}", fileId);
            return ResponseEntity.ok(analysis);
        } catch (FeignException e) {
            logger.error("Error fetching file from storage service. File ID: {}, Status: {}, Message: {}", 
                fileId, e.status(), e.getMessage());
            return ResponseEntity
                .status(e.status())
                .body("Error fetching file from storage service: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error analyzing file. File ID: {}, Error: {}", fileId, e.getMessage(), e);
            return ResponseEntity
                .internalServerError()
                .body("Error analyzing file: " + e.getMessage());
        }
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<?> getAnalysis(@PathVariable Long analysisId) {
        try {
            logger.info("Fetching analysis by ID: {}", analysisId);
            return analysisService.getAnalysisById(analysisId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching analysis. Analysis ID: {}, Error: {}", analysisId, e.getMessage(), e);
            return ResponseEntity
                .internalServerError()
                .body("Error fetching analysis: " + e.getMessage());
        }
    }

    @GetMapping("/by-file/{fileId}")
    public ResponseEntity<?> getAnalysesByFileId(@PathVariable Long fileId) {
        try {
            logger.info("Fetching analyses for file ID: {}", fileId);
            List<TextAnalysis> analyses = analysisService.getAnalysesByFileId(fileId);
            if (analyses.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(analyses);
        } catch (Exception e) {
            logger.error("Error fetching analyses. File ID: {}, Error: {}", fileId, e.getMessage(), e);
            return ResponseEntity
                .internalServerError()
                .body("Error fetching analyses: " + e.getMessage());
        }
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<?> deleteAnalysis(@PathVariable Long analysisId) {
        try {
            logger.info("Deleting analysis with ID: {}", analysisId);
            if (analysisService.deleteAnalysis(analysisId)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting analysis. Analysis ID: {}, Error: {}", analysisId, e.getMessage(), e);
            return ResponseEntity
                .internalServerError()
                .body("Error deleting analysis: " + e.getMessage());
        }
    }
} 