package com.textscanner.analysis.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "file-storage", url = "${file.storage.url:http://file-storing-service:8081}")
public interface FileStorageClient {
    
    @GetMapping("/api/files/{id}")
    byte[] getFile(@PathVariable("id") Long id);
    
    @GetMapping("/api/files/check/{hash}")
    List<Long> checkFileExists(@PathVariable("hash") String hash, @RequestParam(required = false) Long excludeId);
} 