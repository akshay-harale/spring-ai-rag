package com.rag.controller;

import com.rag.service.document.DocumentService;
import com.rag.service.embedding.EmbeddingService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final DocumentService documentService;

    
    private final EmbeddingService embeddingService;

    @GetMapping("/chat")
    @ResponseBody
    public String chat(@RequestParam("message") String message) {
        return embeddingService.queryLLM(message);
    }

    @GetMapping("/loadDocument")
    @ResponseBody
    public void loadDocuments() throws Exception {
        embeddingService.generateEmbedding();
    }
}
