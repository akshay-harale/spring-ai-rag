package com.rag.service.document;

import com.rag.entity.DocumentStatus;
import com.rag.repository.DocumentStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentStatusRepository documentStatusRepository;

    public DocumentStatus initializeDocumentProcessing(String documentName, String documentType) {
        DocumentStatus status = new DocumentStatus(documentName);
        status.setDocumentType(documentType);
        return documentStatusRepository.save(status);
    }

    public void updateDocumentStatus(UUID documentId, String status, String errorMessage, Integer chunkCount) {
        DocumentStatus docStatus = documentStatusRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));

        docStatus.setStatus(status);
        docStatus.setLastUpdated(LocalDateTime.now());
        docStatus.setErrorMessage(errorMessage);
        docStatus.setChunkCount(chunkCount);

        documentStatusRepository.save(docStatus);
    }

    public Optional<DocumentStatus> getDocumentStatus(UUID documentId) {
        return documentStatusRepository.findById(documentId);
    }

    public Optional<DocumentStatus> getLatestDocumentStatus() {
        return documentStatusRepository.findFirstByOrderByLastUpdatedDesc();
    }

    public boolean isDocumentReady() {
        return getLatestDocumentStatus()
                .map(status -> "COMPLETED".equals(status.getStatus()))
                .orElse(false);
    }
}
