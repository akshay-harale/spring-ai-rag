package com.rag.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "document_status")
@Data
@NoArgsConstructor
public class DocumentStatus {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    private String documentName;
    private String status; // PROCESSING, COMPLETED, FAILED
    private LocalDateTime lastUpdated;
    private String errorMessage;
    private Integer chunkCount;
    private String documentType;

    public DocumentStatus(String documentName) {
        this.documentName = documentName;
        this.status = "PROCESSING";
        this.lastUpdated = LocalDateTime.now();
    }
}
