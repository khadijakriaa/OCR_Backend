package com.msi.springsecExample.entity;

import jakarta.persistence.*; // ✅ THIS LINE IS IMPORTANT
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "file_texts")
public class FileText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
}
