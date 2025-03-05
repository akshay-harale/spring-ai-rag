package com.rag.service.embedding;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileUrlResource;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import java.net.URL;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmbeddingService {

    private final VectorStore vectorStore;
    private final ChatClient ragChatClient;
    private final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;
    private final com.rag.service.document.DocumentService documentService;

    private String SYSTEM_PROMPT = """
            You are a knowledgeable assistant that helps users understand document content. Your responses should be:

            1. ACCURATE: Only provide information that is explicitly supported by the given document context
            2. FOCUSED: Answer specifically about the provided document content
            3. TRANSPARENT: If the document context doesn't contain enough information to fully answer a question, acknowledge this limitation
            4. NATURAL: Present information in a clear, conversational manner while maintaining professional tone
            5. CONCISE: Provide direct answers without unnecessary elaboration
            6. CONTEXTUAL: Reference specific sections or quotes from the document when relevant

            Rules:
            - If asked about topics outside the document context, state that you can only provide information from the available document
            - Do not make assumptions beyond what is explicitly stated in the document
            - If a question is ambiguous, ask for clarification about which aspect of the document they want to know about
            - When citing from the document, specify the relevant section or context
            - If you're unsure about any information, admit uncertainty rather than guessing

            Format your responses as follows:
            1. Direct answer to the question
            2. Supporting evidence from the document (if applicable)
            3. Any relevant caveats or limitations about the answer

            Remember: You are analyzing and explaining document content, not creating new information or making assumptions beyond the provided context.
            """;


    public void generateEmbedding(MultipartFile file) {
        try {
            String documentName = file.getOriginalFilename();
            String documentType = getDocumentType(documentName);
            UUID documentId = documentService.initializeDocumentProcessing(documentName, documentType).getId();

            java.io.File tempFile = Files.createTempFile("temp", file.getOriginalFilename()).toFile();
            file.transferTo(tempFile);
            Resource resource = new FileUrlResource(tempFile.toURI().toURL());

            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource,
                    PdfDocumentReaderConfig.builder()
                            .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                    .withNumberOfBottomTextLinesToDelete(3)
                                    .withNumberOfTopPagesToSkipBeforeDelete(1)
                                    .build())
                            .build());
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

            log.info("Parsing document, splitting, creating embeddings and storing in vector store...");

            // tag as external knowledge in the vector store's metadata
            List<Document> splitDocuments = tokenTextSplitter.split(pdfReader.read());
            for (Document splitDocument: splitDocuments) { // footnotes
                splitDocument.getMetadata().put("filename", documentName);
                splitDocument.getMetadata().put("documentId", documentId);
                splitDocument.getMetadata().put("version", 1);
            }

            // Sending batch of documents to vector store
            // Load
            vectorStore.write(splitDocuments);
            documentService.updateDocumentStatus(documentId, "COMPLETED", null, splitDocuments.size());
            log.info("Done parsing document, splitting, creating embeddings and storing in vector store.");
        } catch (Exception e) {
            log.error("Error processing document", e);
            documentService.updateDocumentStatus(UUID.randomUUID(), "FAILED", e.getMessage(), null);
            throw new RuntimeException("Failed to process document: " + e.getMessage(), e);
        }
    }

    private String getDocumentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        return extension.toUpperCase();
    }

    public String queryLLM(String question, UUID documentId) {
       try {
            log.info("Querying LLM with question: {} and documentId: {}", question, documentId);

            String augmentedQuestion = question;
            if (documentId != null) {
                augmentedQuestion = question + " from document " + documentId;
            }


            RetrievalAugmentationAdvisor advisor = retrievalAugmentationAdvisor;

            String response = ragChatClient.prompt()
                    .advisors(advisor)
                    .user(augmentedQuestion)
                    .call()
                    .content();
            log.info("LLM response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error querying LLM", e);
            throw new RuntimeException("Failed to process query: " + e.getMessage(), e);
        }
    }

    public String queryLLM(String question) {
        return queryLLM(question, null);
    }
}
