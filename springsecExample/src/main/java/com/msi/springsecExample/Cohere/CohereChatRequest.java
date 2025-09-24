package com.msi.springsecExample.Cohere;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class CohereChatRequest {

    // The model is now REQUIRED (e.g., "command-r-plus")
    @JsonProperty("model")
    private String model = "command-r-plus";

    // The old 'prompt' field is REPLACED by a 'message' field and a 'preamble'
    @JsonProperty("message")
    private String message; // This is the user's input (equivalent to your old prompt)

    @JsonProperty("preamble")
    private String preamble; // This is for the system instructions (the first part of your old prompt)

    @JsonProperty("max_tokens")
    private int maxTokens = 1500;

    @JsonProperty("temperature")
    private double temperature = 0;

    // Optional: The chat endpoint can also take a 'chat_history' array for conversational context
    // We don't need it for a one-off request, so it's omitted.

    // Constructor now takes both the system instruction and the user message
    public CohereChatRequest(String preamble, String message) {
        this.preamble = preamble;
        this.message = message;
    }

    // Getters and setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPreamble() {
        return preamble;
    }

    public void setPreamble(String preamble) {
        this.preamble = preamble;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}