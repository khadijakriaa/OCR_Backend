package com.msi.springsecExample.Service;

import com.msi.springsecExample.Cohere.CohereChatRequest;
import com.msi.springsecExample.Cohere.CohereChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CohereService {

    @Value("${cohere.api.key}")
    private String cohereApiKey;

    // CORRECTED API ENDPOINT
    private static final String COHERE_API_URL = "https://api.cohere.ai/v1/chat";

    public String sendToCohere(String extractedText) {
        if (extractedText == null || extractedText.trim().isEmpty()) {
            return "Texte vide : rien à envoyer à Cohere.";
        }

        // SPLIT YOUR OLD PROMPT INTO TWO PARTS:
        // 1. The SYSTEM PROMPT (preamble) - The instructions
        String systemInstructions = """
            Tu es un assistant intelligent qui lit une fiche de paie française.
            Ta tâche est d'extraire les informations importantes et de répondre uniquement avec un objet JSON, sans aucun texte supplémentaire.
            Utilise exactement ce format :

            {
              "nom_employe": "string",
              "adresse_employeur": "string",
              "Emploi": "string",
              "date_paie": "string",
              "salaire_brut": "string",
              "salaire_net": "string",
              "impots": [
                {
                  "type": "string",
                  "montant": "string"
                }
              ],
              "deductions": [
                {
                  "type": "string",
                  "montant": "string"
                }
              ],
              "heures_travaillees": "string",
              "taux_horaire": "string"
            }

            Si une information est absente ou illisible, utilise "non spécifié" ou null.
            """;

        // 2. The USER MESSAGE (message) - The data to process
        String userMessage = "Voici le texte brut de la fiche de paie :\n" + extractedText;

        // Create the new request object with both parts
        CohereChatRequest request = new CohereChatRequest(systemInstructions, userMessage);
        // You can still set model and other parameters if needed
        request.setModel("command-r-plus");
        request.setMaxTokens(1500);
        request.setTemperature(0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(cohereApiKey);

        HttpEntity<CohereChatRequest> entity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            // Send the request to the NEW /chat endpoint
            ResponseEntity<CohereChatResponse> response = restTemplate.exchange(
                    COHERE_API_URL, // NOW USING THE CHAT URL
                    HttpMethod.POST,
                    entity,
                    CohereChatResponse.class // Expecting the new response type
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String rawResponse = response.getBody().getResponseText();
                System.out.println("Raw response from Cohere: " + rawResponse);

                // The new API is better at following instructions, but we still clean it.
                return extractJsonFromText(rawResponse);
            } else {
                return "Erreur : réponse invalide de Cohere. Statut: " + response.getStatusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'appel à l'API Cohere: " + e.getMessage();
        }
    }

    /**
     * Extracts JSON substring from a text by finding the first '{' and last '}'.
     * If no JSON found, returns original text.
     * (This method can remain the same)
     */
    private String extractJsonFromText(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1).trim();
        }
        return text;
    }
}