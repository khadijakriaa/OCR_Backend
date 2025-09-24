package com.msi.springsecExample.dto;

public class PayslipResponse {

        private String extractedText;
        private String cohereResult;

        // Getters and setters
        public String getExtractedText() {
            return extractedText;
        }

        public void setExtractedText(String extractedText) {
            this.extractedText = extractedText;
        }

        public String getCohereResult() {
            return cohereResult;
        }

        public void setCohereResult(String cohereResult) {
            this.cohereResult = cohereResult;
        }

}
