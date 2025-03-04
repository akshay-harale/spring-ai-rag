package com.rag.service.embedding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.springframework.ai.embedding.EmbeddingResultMetadata;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class EmbeddingResultMetadataUserType implements UserType<EmbeddingResultMetadata> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<EmbeddingResultMetadata> returnedClass() {
        return EmbeddingResultMetadata.class;
    }

    @Override
    public boolean equals(EmbeddingResultMetadata x, EmbeddingResultMetadata y) {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(EmbeddingResultMetadata x) {
        return x.hashCode();
    }

    @Override
    public EmbeddingResultMetadata nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.readValue(value, EmbeddingResultMetadata.class);
        } catch (JsonProcessingException e) {
            throw new HibernateException("Error deserializing EmbeddingResultMetadata", e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, EmbeddingResultMetadata value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            try {
                st.setString(index, objectMapper.writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new HibernateException("Error serializing EmbeddingResultMetadata", e);
            }
        }
    }

    @Override
    public EmbeddingResultMetadata deepCopy(EmbeddingResultMetadata value) {
        if (value == null) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(value);
            return objectMapper.readValue(json, EmbeddingResultMetadata.class);
        } catch (JsonProcessingException e) {
            throw new HibernateException("Error copying EmbeddingResultMetadata", e);
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(EmbeddingResultMetadata value) {
        try {
            return value == null ? null : objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new HibernateException("Error disassembling EmbeddingResultMetadata", e);
        }
    }

    @Override
    public EmbeddingResultMetadata assemble(Serializable cached, Object owner) {
        try {
            return cached == null ? null : objectMapper.readValue((String) cached, EmbeddingResultMetadata.class);
        } catch (JsonProcessingException e) {
            throw new HibernateException("Error assembling EmbeddingResultMetadata", e);
        }
    }

    @Override
    public EmbeddingResultMetadata replace(EmbeddingResultMetadata detached, EmbeddingResultMetadata managed, Object owner) {
        return deepCopy(detached);
    }
}
