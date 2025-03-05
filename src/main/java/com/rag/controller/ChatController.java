package com.rag.controller;

import com.rag.entity.DocumentStatus;
import com.rag.service.document.DocumentService;
import com.rag.service.embedding.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final DocumentService documentService;
    private final EmbeddingService embeddingService;

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam("message") String message, @RequestParam(value = "documentId", required = false) UUID documentId) {
        try {
            String response = embeddingService.queryLLM(message, documentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing query: " + e.getMessage());
        }
    }

    @PostMapping(value="/loadDocument", consumes = {"multipart/form-data"})
    public ResponseEntity<String> loadDocument(@RequestParam("file") MultipartFile file) {
        try {
            embeddingService.generateEmbedding(file);
            return ResponseEntity.ok("Document loaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading document: " + e.getMessage());
        }
    }

    @GetMapping("/documentStatus")
    public ResponseEntity<DocumentStatus> getDocumentStatus() {
        return documentService.getLatestDocumentStatus()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
