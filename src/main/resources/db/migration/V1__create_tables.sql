CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.vector_store (
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	"content" text NULL,
	metadata json NULL,
	embedding public.vector(1536) NULL,
	CONSTRAINT vector_store_pkey PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS spring_ai_vector_index ON public.vector_store USING hnsw (embedding vector_cosine_ops);

CREATE TABLE public.document_status (
	id uuid NOT NULL,
	chunk_count int4 NULL,
	last_updated timestamp(6) NULL,
	document_name varchar(255) NULL,
	document_type varchar(255) NULL,
	error_message varchar(255) NULL,
	status varchar(255) NULL,
	CONSTRAINT document_status_pkey PRIMARY KEY (id)
);
