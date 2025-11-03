package com.msi.springsecExample.Cohere;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CohereChatResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("text")
    private String text;

    @JsonProperty("message")
    private Message message;

    @JsonProperty("content")
    private List<Content> content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    /**
     * Returns the response text from any possible field in the response
     */
    public String getResponseText() {
        // Try different response formats
        if (text != null && !text.trim().isEmpty()) {
            return text;
        }
        if (message != null && message.getText() != null) {
            return message.getText();
        }
        if (content != null && !content.isEmpty()) {
            return content.get(0).getText();
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        @JsonProperty("text")
        private String text;

        @JsonProperty("role")
        private String role;

        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }

        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        @JsonProperty("type")
        private String type;

        @JsonProperty("text")
        private String text;

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
    }
}