package com.msi.springsecExample.Service;

import com.msi.springsecExample.Cohere.CohereChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class CohereService {
    @Value("${cohere.api.key}")
    private String cohereApiKey;

    @Value("${cohere.model:command-a-03-2025}")
    private String cohereModel;

    @Value("${cohere.api.url:https://api.cohere.ai/v1/chat}")
    private String cohereChatUrl;

    @Value("${cohere.api.version:2024-11-15}")
    private String cohereApiVersion;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Extract structured payslip JSON
    public String extractPayslipData(String ocrText) {
        String prompt = "Tu es un assistant intelligent qui lit une fiche de paie française. "
                + "Extrait uniquement les informations importantes sous forme d'objet JSON avec les champs: "
                + "name, role, age, salary, impots (array), deductions (array). "
                + "Ne renvoie rien d'autre que le JSON valide.\n\nTexte: " + ocrText;

        return sendChatRequest(prompt);
    }

    // Summarize text
    public String summarizeText(String text) {
        String prompt = "Fais un résumé concis du texte suivant en français, sous forme de texte simple :\n\n" + text;
        return sendChatRequest(prompt);
    }

    // Core chat request - FIXED JSON formatting
    private String sendChatRequest(String messageContent) {
        try {
            // Create proper JSON structure using ObjectMapper to avoid syntax errors
            CohereRequest request = new CohereRequest();
            request.setModel(cohereModel);
            request.setMessage(messageContent);
            request.setMaxTokens(1500);
            request.setTemperature(0.0);
            request.setPreamble("You are a helpful assistant that responds in French.");

            String requestBody = objectMapper.writeValueAsString(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Bearer " + cohereApiKey);
            headers.set("Cohere-Version", cohereApiVersion);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // First, get the raw response as String to see what we're getting
            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    cohereChatUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("Cohere API Response: " + rawResponse.getBody());

            if (rawResponse.getStatusCode().is2xxSuccessful() && rawResponse.getBody() != null) {
                try {
                    CohereChatResponse response = objectMapper.readValue(rawResponse.getBody(), CohereChatResponse.class);
                    String result = response.getResponseText();
                    return result != null ? result.trim() : "No response text received";
                } catch (Exception e) {
                    // If parsing fails, return the raw response
                    return "Raw response: " + rawResponse.getBody();
                }
            } else {
                return "Erreur : réponse invalide de Cohere. Statut: " + rawResponse.getStatusCode() + " - Body: " + rawResponse.getBody();
            }

        } catch (HttpClientErrorException e) {
            String errorDetails = e.getResponseBodyAsString();
            System.err.println("Cohere API Error: " + errorDetails);
            throw new RuntimeException("Cohere API call failed: " + e.getStatusCode() + " - " + errorDetails);
        } catch (Exception e) {
            System.err.println("General Error: " + e.getMessage());
            throw new RuntimeException("Error calling Cohere API: " + e.getMessage(), e);
        }
    }

    // Inner class for proper JSON serialization
    public static class CohereRequest {
        private String model;
        private String message;
        private int maxTokens;
        private double temperature;
        private String preamble;

        // Getters and setters
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }

        public String getPreamble() { return preamble; }
        public void setPreamble(String preamble) { this.preamble = preamble; }
    }
}