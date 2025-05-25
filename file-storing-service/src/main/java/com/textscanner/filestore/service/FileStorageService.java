package com.textscanner.filestore.service;

import com.textscanner.filestore.model.StoredFile;
import com.textscanner.filestore.repository.StoredFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.List;

@Service
public class FileStorageService {
    
    @Autowired
    private StoredFileRepository fileRepository;
    
    @Transactional
    public StoredFile store(MultipartFile file) throws IOException {
        StoredFile storedFile = new StoredFile();
        storedFile.setFilename(file.getOriginalFilename());
        storedFile.setContent(file.getBytes());
        storedFile.setContentType(file.getContentType());
        storedFile.setUploadDate(LocalDateTime.now());
        storedFile.setHash(calculateHash(file.getBytes()));
        return fileRepository.save(storedFile);
    }
    
    @Transactional(readOnly = true)
    public Optional<StoredFile> getFile(Long id) {
        return fileRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByHash(String hash) {
        return !fileRepository.findByHash(hash).isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean existsByHashExcludingId(String hash, Long excludeId) {
        List<StoredFile> files = fileRepository.findByHash(hash);
        if (excludeId == null) {
            return !files.isEmpty();
        }
        return files.stream().anyMatch(file -> !file.getId().equals(excludeId));
    }
    
    @Transactional(readOnly = true)
    public List<Long> findIdsByHashExcludingId(String hash, Long excludeId) {
        List<StoredFile> files = fileRepository.findByHash(hash);
        if (excludeId == null) {
            return files.stream()
                    .map(StoredFile::getId)
                    .toList();
        }
        return files.stream()
                .filter(file -> !file.getId().equals(excludeId))
                .map(StoredFile::getId)
                .toList();
    }
    
    private String calculateHash(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not generate hash", e);
        }
    }

    @Transactional
    public boolean deleteFile(Long id) {
        if (fileRepository.existsById(id)) {
            fileRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 