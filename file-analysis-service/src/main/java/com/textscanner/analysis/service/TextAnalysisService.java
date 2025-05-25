package com.textscanner.analysis.service;

import com.textscanner.analysis.client.FileStorageClient;
import com.textscanner.analysis.model.TextAnalysis;
import com.textscanner.analysis.repository.TextAnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class TextAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(TextAnalysisService.class);

    @Autowired
    private TextAnalysisRepository analysisRepository;

    @Autowired
    private FileStorageClient fileStorageClient;

    public TextAnalysis analyzeFile(Long fileId) {
        logger.info("Fetching file content for analysis, fileId: {}", fileId);
        byte[] fileContent = fileStorageClient.getFile(fileId);
        if (fileContent == null || fileContent.length == 0) {
            logger.error("Received empty file content for fileId: {}", fileId);
            throw new IllegalArgumentException("File content is empty");
        }

        String text = new String(fileContent, StandardCharsets.UTF_8);
        logger.debug("File content converted to text, length: {}", text.length());
        
        TextAnalysis analysis = new TextAnalysis();
        analysis.setFileId(fileId);
        
        String[] paragraphs = text.split("\n\\s*\n");
        analysis.setParagraphCount(paragraphs.length);
        logger.debug("Paragraph count: {}", paragraphs.length);
        
        String[] words = text.split("\\s+");
        analysis.setWordCount(words.length);
        logger.debug("Word count: {}", words.length);
        
        int charCount = (int) text.chars().filter(ch -> !Character.isWhitespace(ch)).count();
        analysis.setCharacterCount(charCount);
        logger.debug("Character count: {}", charCount);
        
        String hash = calculateHash(text);
        analysis.setFileHash(hash);
        logger.debug("Calculated hash: {}", hash);
        
        List<Long> matchingFileIds = fileStorageClient.checkFileExists(hash, fileId);
        boolean hasPlagiarism = !matchingFileIds.isEmpty();
        
        analysis.setHasPlagiarism(hasPlagiarism);
        analysis.setPlagiarismMatchFileIds(matchingFileIds);
        logger.info("Plagiarism check result: {}, matching file IDs: {}", hasPlagiarism, matchingFileIds);
        
        TextAnalysis savedAnalysis = analysisRepository.save(analysis);
        logger.info("Analysis saved successfully for fileId: {}", fileId);
        
        return savedAnalysis;
    }
    
    private String calculateHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error calculating hash", e);
            throw new RuntimeException("Could not generate hash", e);
        }
    }
    
    public Optional<TextAnalysis> getAnalysisById(Long id) {
        logger.info("Fetching analysis by ID: {}", id);
        return analysisRepository.findById(id);
    }
    
    public List<TextAnalysis> getAnalysesByFileId(Long fileId) {
        logger.info("Fetching analyses for fileId: {}", fileId);
        return analysisRepository.findByFileId(fileId);
    }

    @Transactional
    public boolean deleteAnalysis(Long id) {
        if (analysisRepository.existsById(id)) {
            analysisRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 