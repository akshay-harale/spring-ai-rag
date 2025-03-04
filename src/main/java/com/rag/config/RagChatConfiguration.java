package com.rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagChatConfiguration {

    String SYSTEM_PROMPT = """
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

    @Bean
    ChatClient ragChatClient(ChatClient.Builder chatClientBuilder,
                             RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
        return chatClientBuilder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        retrievalAugmentationAdvisor
                )
                .build();
    }

    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore) {
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.50)
                .topK(5)
                .build();

        ContextualQueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder()
                .allowEmptyContext(true)
                .promptTemplate(new PromptTemplate(SYSTEM_PROMPT))
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(queryAugmenter)
                .build();
    }
}
