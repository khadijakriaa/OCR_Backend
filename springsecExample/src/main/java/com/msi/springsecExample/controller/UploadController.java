package com.msi.springsecExample.controller;

import com.msi.springsecExample.Service.TextExtractionService;
import com.msi.springsecExample.Service.CohereService;
import com.msi.springsecExample.dto.PayslipResponse;
import com.msi.springsecExample.entity.FileText;
import com.msi.springsecExample.Repository.FileTextRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:4200")
public class UploadController {

    @Autowired
    private TextExtractionService textExtractionService;

    @Autowired
    private FileTextRepository fileTextRepository;

    @Autowired
    private CohereService cohereService;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "mode", defaultValue = "structured") String mode) {

        try {
            // Step 1: Extract text
            String extractedText = textExtractionService.extractTextFromFile(file);

            // Step 2: Send to Cohere depending on mode
            String cohereResult;
            if ("summary".equalsIgnoreCase(mode)) {
                cohereResult = cohereService.summarizeText(extractedText);
            } else {
                cohereResult = cohereService.extractPayslipData(extractedText);
            }

            // Step 3: Save original file & extracted text
            FileText fileText = new FileText();
            fileText.setFilename(file.getOriginalFilename());
            fileText.setContent(extractedText);
            fileText.setFileData(file.getBytes());
            fileTextRepository.save(fileText);

            // Step 4: Prepare response
            PayslipResponse response = new PayslipResponse();
            response.setExtractedText(extractedText);
            response.setCohereResult(cohereResult);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllTexts() {
        return ResponseEntity.ok(fileTextRepository.findAll());
    }
}
