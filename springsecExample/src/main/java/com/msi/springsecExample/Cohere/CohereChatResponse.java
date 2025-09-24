package com.msi.springsecExample.Cohere;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CohereChatResponse {

    // The old 'generations' list is REPLACED by a 'text' field
    @JsonProperty("text")
    private String text; // The direct response text from the model

    // Getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Helper method to get the text
    public String getResponseText() {
        return text;
    }
}