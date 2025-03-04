package com.rag.service.document;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DocumentService {

    public String loadDocument(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error loading document";
        }
    }
}
