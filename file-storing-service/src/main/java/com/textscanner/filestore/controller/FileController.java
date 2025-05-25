package com.textscanner.filestore.controller;

import com.textscanner.filestore.model.StoredFile;
import com.textscanner.filestore.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@Tag(name = "File Storage", description = "API for managing text files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Operation(
        summary = "Upload a text file",
        description = "Upload a text file (.txt) for analysis. The file will be stored and a unique ID will be returned."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "File uploaded successfully",
            content = @Content(schema = @Schema(implementation = StoredFile.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid file format or empty file"
        )
    })
    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    public ResponseEntity<StoredFile> uploadFile(
        @Parameter(
            description = "Text file to upload (.txt format)",
            required = true
        )
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (!file.getContentType().equals("text/plain")) {
            return ResponseEntity.badRequest().build();
        }
        StoredFile storedFile = fileStorageService.store(file);
        return ResponseEntity.ok(storedFile);
    }

    @Operation(
        summary = "Download a file",
        description = "Download a previously uploaded file by its ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "File downloaded successfully",
            content = @Content(mediaType = "text/plain")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "File not found"
        )
    })
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getFile(
        @Parameter(description = "ID of the file to download")
        @PathVariable Long id
    ) {
        return fileStorageService.getFile(id)
                .map(file -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                        .body(file.getContent()))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Check file existence",
        description = "Check if files with the given hash exist and return their IDs (used for plagiarism detection)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Check completed",
            content = @Content(schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/check/{hash}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Long>> checkFileExists(
        @Parameter(description = "SHA-256 hash of the file content")
        @PathVariable String hash,
        @Parameter(description = "ID of the file to exclude from the check")
        @RequestParam(required = false) Long excludeId
    ) {
        return ResponseEntity.ok(fileStorageService.findIdsByHashExcludingId(hash, excludeId));
    }

    @Operation(
        summary = "Delete a file",
        description = "Delete a previously uploaded file by its ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "File deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "File not found"
        )
    })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteFile(
        @Parameter(description = "ID of the file to delete")
        @PathVariable Long id
    ) {
        if (fileStorageService.deleteFile(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
} 