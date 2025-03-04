package com.rag.service.embedding;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmbeddingService {

    private final VectorStore vectorStore;

    private final ChatClient ragChatClient;

    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;


    public void generateEmbedding()  throws Exception{
        Resource pdfResource = new ClassPathResource("document.pdf");
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource,
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfBottomTextLinesToDelete(3) // Specifies that the bottom 3 lines of text on each page should be deleted.
                                .withNumberOfTopPagesToSkipBeforeDelete(1) // Indicates that the text deletion rule should not apply to the first page.
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

        log.info("Parsing document, splitting, creating embeddings, and storing in vector store...");

        // tag as external knowledge in the vector store's metadata
        List<Document> splitDocuments = tokenTextSplitter.split(pdfReader.read());
        for (Document splitDocument: splitDocuments) { // footnotes
            splitDocument.getMetadata().put("filename", pdfResource.getFilename());
            splitDocument.getMetadata().put("version", 1);
        }

        // Sending batch of documents to vector store
        // Load
        vectorStore.write(splitDocuments);

        log.info("Done parsing document, splitting, creating embeddings and storing in vector store.");
    }

    public String queryLLM(String question) {
        return ragChatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user(question)
                .call()
                .content();
    }
}
