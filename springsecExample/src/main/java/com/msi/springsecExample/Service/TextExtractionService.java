package com.msi.springsecExample.Service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class TextExtractionService {

    private static final Logger logger = LoggerFactory.getLogger(TextExtractionService.class);
    private static final String ERROR_NULL_FILE = "Input file cannot be null";
    private static final String ERROR_EMPTY_FILE = "Input file is empty";

    @Value("${tesseract.datapath:C:/Program Files/Tesseract-OCR}")
    private String tessDataPath;

    public String extractTextFromFile(MultipartFile file) throws IOException, TesseractException {
        if (file == null) {
            throw new IllegalArgumentException(ERROR_NULL_FILE);
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException(ERROR_EMPTY_FILE);
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        logger.info("Extracting text from file: {}", filename);

        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return extractTextFromPDF(file);
        } else if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return extractTextFromImage(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + filename);
        }
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    // In the extractTextFromImage method, add French language support:
    private String extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            throw new IOException("Could not determine file extension.");
        }

        File tempFile = File.createTempFile("upload-", extension);

        try {
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessDataPath);
            // Add French language for better payslip recognition
            tesseract.setLanguage("eng+fra");
            tesseract.setPageSegMode(6); // Uniform block of text
            tesseract.setOcrEngineMode(1);

            // Improve image processing for documents
            tesseract.setTessVariable("user_defined_dpi", "300");
            tesseract.setTessVariable("preserve_interword_spaces", "1");

            logger.info("Performing OCR on image file: {}", tempFile.getAbsolutePath());
            String result = tesseract.doOCR(tempFile);
            logger.info("OCR completed successfully");
            return result;

        } catch (TesseractException e) {
            logger.error("OCR failed: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    logger.warn("Temporary file could not be deleted: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }
}
