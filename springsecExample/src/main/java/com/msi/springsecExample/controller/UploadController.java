package com.msi.springsecExample.Controller;

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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:4200")
public class UploadController {

    @Autowired
    private TextExtractionService textExtractionService;

    @Autowired
    private FileTextRepository fileTextRepository;

    @Autowired
    private CohereService cohereService; // 👈 Inject the Cohere service

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Step 1: OCR extraction
            String extractedText = textExtractionService.extractTextFromFile(file);

            // Step 2: Send to Cohere
            String cohereResult = cohereService.sendToCohere(extractedText);

            // Step 3: Save extracted text to DB (if you want to also store Cohere result, update the entity)
            FileText fileText = new FileText();
            fileText.setFilename(file.getOriginalFilename());
            fileText.setContent(extractedText);
            fileTextRepository.save(fileText);

            // Step 4: Return response containing both extracted and analyzed text
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


