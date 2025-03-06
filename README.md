# Spring AI Rag

**Note:** The Docker setup for Ollama is not working as expected. Please set up Ollama locally without Docker.

## Introduction
Spring AI Rag is a configuration project for setting up a chat client with retrieval-augmented generation (RAG) capabilities. This project uses Spring Framework with Ollama for LLM integration and pgvector for vector similarity search.

## Prerequisites

### 1. Docker Setup
- Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- Ensure Docker Compose is installed (included with Docker Desktop)
- Minimum system requirements:
  - 8GB RAM recommended
  - x86_64 or ARM64 processor

### 2. PostgreSQL with pgvector
- No manual installation required
- The application uses pgvector/pgvector:0.8.0-pg17
- Automatically configured through Docker Compose

### 3. Ollama Setup
- Automatically configured through Docker Compose
- Uses llama3.2 model
- No manual installation required when using Docker setup

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/akshay-harale/spring-ai-rag.git
   ```

2. Navigate to the project directory:
   ```bash
   cd spring-ai-rag
   ```

3. Start the application using Docker Compose:
   ```bash
   docker-compose up -d
   ```
   This will:
   - Start PostgreSQL with pgvector extension
   - Launch Ollama service with llama3.2 model
   - Build and run the Spring Boot application

4. Verify all services are running:
   ```bash
   docker-compose ps
   ```

## Configuration

### Database Configuration
- Database Name: ragdb
- Default User: raguser
- Default Password: ragpass
- Port: 5432

### Ollama Configuration
- Base URL: http://localhost:11434/
- Model: llama3.2
- Temperature: 0.7

### Vector Store Configuration
- Index Type: HNSW (Hierarchical Navigable Small World)
- Distance Type: COSINE_DISTANCE
- Dimensions: 384
- Batching Strategy: TOKEN_COUNT
- Max Document Batch Size: 10000

## Usage
The application will be available at http://localhost:8080

## Contributing
If you wish to contribute to this project:
1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Commit your changes and push them to your fork.
4. Open a pull request with a description of your changes.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
