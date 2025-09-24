package com.fingerprint.controller;

import com.fingerprint.dto.FingerprintRequest;
import com.fingerprint.dto.FingerprintResponse;
import com.fingerprint.service.FingerprintProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fingerprint")
@CrossOrigin(origins = "*")
public class FingerprintController {
    
    @Autowired
    private FingerprintProcessingService fingerprintService;
    
    @PostMapping("/process")
    public ResponseEntity<FingerprintResponse> processFingerprints(
            @RequestBody FingerprintRequest request) {
        
        try {
            String xmlTemplate = fingerprintService.processFingerprints(request);
            
            FingerprintResponse response = new FingerprintResponse(
                xmlTemplate, 
                "SUCCESS", 
                "Fingerprint template created successfully"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            FingerprintResponse errorResponse = new FingerprintResponse(
                null, 
                "ERROR", 
                e.getMessage()
            );
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Fingerprint service is running");
    }

    @GetMapping("/test/sample-request")
    public ResponseEntity<FingerprintRequest> getSampleRequest(@RequestParam(defaultValue = "RAW") String format) {
        FingerprintRequest request = new FingerprintRequest();
        request.setDeviceUid("TEST-DEVICE-001");
        
        // Create sample fingerprint data
        FingerprintRequest.FingerprintSample sample = new FingerprintRequest.FingerprintSample();
        
        if ("RAW".equalsIgnoreCase(format)) {
            // Sample RAW format data
            request.setSampleFormatValue(1); // RAW
            
            // Create header for RAW format
            FingerprintRequest.Header header = new FingerprintRequest.Header();
            header.setWidth(500);
            header.setHeight(500);
            header.setDpi(500);
            header.setQuality(90);
            sample.setHeader(header);
            
            // Simulate raw image data (this is just a placeholder)
            byte[] rawData = new byte[500 * 500]; // Create dummy image data
            for (int i = 0; i < rawData.length; i++) {
                rawData[i] = (byte)(i % 256);
            }
            sample.setData(java.util.Base64.getEncoder().encodeToString(rawData));
            
        } else {
            // Sample INTERMEDIATE format data
            request.setSampleFormatValue(2); // INTERMEDIATE
            
            // Create header for INTERMEDIATE format
            FingerprintRequest.Header header = new FingerprintRequest.Header();
            header.setQuality(90);
            sample.setHeader(header);
            
            // Simulate intermediate template data (this is just a placeholder)
            byte[] templateData = new byte[512]; // Create dummy template data
            for (int i = 0; i < templateData.length; i++) {
                templateData[i] = (byte)(i % 256);
            }
            sample.setData(java.util.Base64.getEncoder().encodeToString(templateData));
        }
        
        // Add the sample to the request
        request.setSamples(java.util.Arrays.asList(sample));
        
        return ResponseEntity.ok(request);
    }
}