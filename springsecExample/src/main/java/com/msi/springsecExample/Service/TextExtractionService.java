package com.msi.springsecExample.Service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class TextExtractionService {

    private static final Logger logger = LoggerFactory.getLogger(TextExtractionService.class);
    private static final String ERROR_NULL_FILE = "Input file cannot be null";
    private static final String ERROR_EMPTY_FILE = "Input file is empty";

    public String extractTextFromFile(MultipartFile file) throws IOException, TesseractException {
        String filename = file.getOriginalFilename();

        if (filename == null) {
            throw new IllegalArgumentException("Invalid file name");
        }

        logger.info("Extracting text from file: {}", filename);

        if (filename.endsWith(".pdf")) {
            return extractTextFromPDF(file);
        } else if (filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return extractTextFromImage(file);
        } else {
            throw new IllegalArgumentException("Unsupfported file type: " + filename);
        }
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

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
            file.transferTo(tempFile);
            System.out.println("1");
            Tesseract tesseract = new Tesseract();
            // NOTE: Use the parent folder of tessdata, NOT tessdata itself
            tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
            tesseract.setLanguage("eng");

            System.out.println("2");
            tesseract.setTessVariable("user_defined_dpi", "300");
            System.out.println("3");
            logger.info("Performing OCR on image file: {}", tempFile.getAbsolutePath());
            System.out.println("4");
            return tesseract.doOCR(tempFile);
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
