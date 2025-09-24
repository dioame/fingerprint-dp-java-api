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
}