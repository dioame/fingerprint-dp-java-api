package com.fingerprint.config;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class DigitalPersonaConfig {
    
    @PostConstruct
    public void initialize() {
        try {
            // Get the appropriate native library source path based on architecture
            String arch = System.getProperty("os.arch");
            File sourceDir;
            
            if (arch.contains("64")) {
                sourceDir = new File("lib/x64");
            } else {
                sourceDir = new File("lib/win32");
            }
            
            // Create a temp directory for the DLLs
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "dpfinger_" + System.currentTimeMillis());
            if (!tempDir.exists() && !tempDir.mkdirs()) {
                throw new RuntimeException("Failed to create temporary directory for native libraries");
            }
            
            // Copy all DLL files to the temp directory
            System.out.println("Copying native libraries from: " + sourceDir.getAbsolutePath());
            System.out.println("To temporary directory: " + tempDir.getAbsolutePath());
            
            File[] dllFiles = sourceDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".dll"));
            if (dllFiles == null || dllFiles.length == 0) {
                throw new RuntimeException("No DLL files found in " + sourceDir.getAbsolutePath());
            }
            
            for (File dllFile : dllFiles) {
                File destFile = new File(tempDir, dllFile.getName());
                java.nio.file.Files.copy(
                    dllFile.toPath(),
                    destFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                System.out.println("Copied: " + dllFile.getName());
            }
            
            // Add the temp directory to java.library.path
            System.setProperty("java.library.path",
                System.getProperty("java.library.path") + File.pathSeparator + tempDir.getAbsolutePath());
            
            // Register shutdown hook to clean up temp files
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    for (File file : tempDir.listFiles()) {
                        file.delete();
                    }
                    tempDir.delete();
                } catch (Exception e) {
                    System.err.println("Failed to cleanup temporary files: " + e.getMessage());
                }
            }));
            
            // The SDK is automatically initialized when we first use it
            // Just verify we can get the Engine instance
            Engine engine = UareUGlobal.GetEngine();
            if (engine != null) {
                System.out.println("DigitalPersona SDK initialized successfully");
            } else {
                System.err.println("Failed to initialize DigitalPersona SDK: Could not get Engine instance");
                throw new RuntimeException("Failed to initialize DigitalPersona SDK");
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize DigitalPersona SDK", e);
        }
    }
}
