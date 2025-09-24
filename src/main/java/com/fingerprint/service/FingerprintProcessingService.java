package com.fingerprint.service;

import com.digitalpersona.uareu.*;
import com.fingerprint.dto.FingerprintRequest;
import com.fingerprint.dto.FingerprintTemplate;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class FingerprintProcessingService {
    
    public String processFingerprints(FingerprintRequest request) {
        try {
            List<FingerprintRequest.FingerprintSample> samples = request.getSamples();
            
            if (samples == null || samples.isEmpty()) {
                throw new IllegalArgumentException("No fingerprint samples provided");
            }
            
            // Initialize the DigitalPersona Engine
            Engine engine = UareUGlobal.GetEngine();
            
            // Process each fingerprint sample
            List<Fmd> fmds = new ArrayList<>();
            for (FingerprintRequest.FingerprintSample sample : samples) {
                byte[] decodedData = Base64.getDecoder().decode(sample.getData());
                
                // Process based on format type
                if (request.getFormat() == FingerprintRequest.Format.INTERMEDIATE) {
                    // Import pre-registered FMD template
                    Fmd fmd = UareUGlobal.GetImporter().ImportFmd(
                        decodedData,
                        Fmd.Format.ISO_19794_2_2005,
                        Fmd.Format.ISO_19794_2_2005
                    );
                    fmds.add(fmd);
                } else if (request.getFormat() == FingerprintRequest.Format.RAW) {
                    // Process raw image data
                    
                    // Get image parameters from header
                    int width = sample.getHeader().getWidth();
                    int height = sample.getHeader().getHeight();
                    int dpi = sample.getHeader().getDpi();
                    
                    // Create FID from raw image data
                    Fid fid = UareUGlobal.GetImporter().ImportFid(
                        decodedData,
                        Fid.Format.ANSI_381_2004
                    );
                    
                    // Create FMD directly from raw image data
                    Fmd fmd = engine.CreateFmd(
                        fid,
                        Fmd.Format.ISO_19794_2_2005
                    );
                    
                    if (fmd != null) {
                        fmds.add(fmd);
                    } else {
                        throw new RuntimeException("Failed to create FMD from raw image data");
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported format type: " + request.getFormat());
                }
            }
            
            // Create enrollment template from processed samples
            if (fmds.isEmpty()) {
                throw new IllegalStateException("No valid fingerprints to process");
            }

            // Process all FMDs to create an enrollment template
            if (fmds.isEmpty()) {
                throw new IllegalStateException("No valid fingerprints to process");
            }

            // Create enrollment callback that will provide all FMDs
            final List<Fmd> finalFmds = new ArrayList<>(fmds);
            Engine.EnrollmentCallback callback = new Engine.EnrollmentCallback() {
                private int currentIndex = 0;
                
                @Override
                public Engine.PreEnrollmentFmd GetFmd(Fmd.Format format) {
                    // Return null when we've provided all FMDs
                    if (currentIndex >= finalFmds.size()) {
                        return null;
                    }
                    
                    // Create a new PreEnrollmentFmd
                    Engine.PreEnrollmentFmd result = new Engine.PreEnrollmentFmd();
                    // Note: Using reflection here because the SDK's PreEnrollmentFmd doesn't provide
                    // a public way to set the FMD. This is a workaround for the SDK's limitation.
                    // In a production environment, you should contact DigitalPersona support for the proper way
                    // to handle this or update to a newer SDK version if available.
                    try {
                        java.lang.reflect.Field fmdField = Engine.PreEnrollmentFmd.class.getDeclaredField("fmd");
                        fmdField.setAccessible(true);
                        fmdField.set(result, finalFmds.get(currentIndex++));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to set FMD in PreEnrollmentFmd", e);
                    }
                    return result;
                }
            };
            
            // Create the enrollment template using all FMDs
            Fmd enrollmentFmd = engine.CreateEnrollmentFmd(
                Fmd.Format.ISO_19794_2_2005,
                callback
            );
            
            if (enrollmentFmd == null) {
                throw new RuntimeException("Failed to create enrollment template");
            }
            
            // Convert the FMD to XML format
            return convertFmdToXml(enrollmentFmd);
            
        } catch (UareUException e) {
            throw new RuntimeException("DigitalPersona SDK error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process fingerprints: " + e.getMessage(), e);
        }
    }
    
    private String convertFmdToXml(Fmd fmd) throws Exception {
        FingerprintTemplate template = new FingerprintTemplate();
        template.setFormat("ISO_19794_2_2005");
        template.setVersion("1.0");
        template.setData(Base64.getEncoder().encodeToString(fmd.getData()));
        
        JAXBContext context = JAXBContext.newInstance(FingerprintTemplate.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(template, baos);
        return baos.toString("UTF-8");
    }
}