package com.msi.springsecExample.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*; // ✅ THIS LINE IS IMPORTANT
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "file_texts")
public class FileText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    // Extracted text (OCR result)
    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // Raw file bytes (original uploaded file)
    @JsonIgnore // avoid returning large base64 payloads in API responses
    @JdbcTypeCode(SqlTypes.BINARY) // ensure Hibernate binds as BYTEA on PostgreSQL
    @Column(name = "file_data", columnDefinition = "bytea")
    private byte[] fileData;

    public FileText() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
