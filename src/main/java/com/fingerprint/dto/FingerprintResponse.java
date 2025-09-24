package com.fingerprint.dto;

public class FingerprintResponse {
    private String xmlTemplate;
    private String status;
    private String message;
    
    public FingerprintResponse() {}
    
    public FingerprintResponse(String xmlTemplate, String status, String message) {
        this.xmlTemplate = xmlTemplate;
        this.status = status;
        this.message = message;
    }
    
    // Getters and setters
    public String getXmlTemplate() { return xmlTemplate; }
    public void setXmlTemplate(String xmlTemplate) { this.xmlTemplate = xmlTemplate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}