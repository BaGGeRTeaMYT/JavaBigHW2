package com.textscanner.filestore.repository;

import com.textscanner.filestore.model.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
    List<StoredFile> findByHash(String hash);
} 