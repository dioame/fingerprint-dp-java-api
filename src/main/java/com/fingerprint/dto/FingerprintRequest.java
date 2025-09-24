package com.fingerprint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FingerprintRequest {
    public enum Format {
        RAW(1),
        INTERMEDIATE(2);
        
        private final int value;
        
        Format(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        public static Format fromValue(int value) {
            for (Format format : Format.values()) {
                if (format.getValue() == value) {
                    return format;
                }
            }
            throw new IllegalArgumentException("Unknown format value: " + value);
        }
    }

    @JsonProperty("deviceUid")
    private String deviceUid;
    
    @JsonProperty("format")
    private Format format;
    
    @JsonProperty("samples")
    private List<FingerprintSample> samples;
    
    // Getters and setters
    public String getDeviceUid() { return deviceUid; }
    public void setDeviceUid(String deviceUid) { this.deviceUid = deviceUid; }
    
    public Format getFormat() { return format; }
    public void setFormat(Format format) { this.format = format; }
    
    @JsonProperty("sampleFormat")
    public void setSampleFormatValue(int value) {
        this.format = Format.fromValue(value);
    }
    
    public List<FingerprintSample> getSamples() { return samples; }
    public void setSamples(List<FingerprintSample> samples) { this.samples = samples; }
    
    public static class FingerprintSample {
        @JsonProperty("Data")
        private String data;
        
        @JsonProperty("Header")
        private Header header;
        
        @JsonProperty("Version")
        private int version;
        
        // Getters and setters
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        
        public Header getHeader() { return header; }
        public void setHeader(Header header) { this.header = header; }
        
        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
    }
    
    public static class Header {
        @JsonProperty("Encryption")
        private int encryption;
        
        @JsonProperty("Factor")
        private int factor;
        
        @JsonProperty("Format")
        private FormatInfo formatInfo;
        
        @JsonProperty("Purpose")
        private int purpose;
        
        @JsonProperty("Quality")
        private int quality;
        
        @JsonProperty("Type")
        private int type;
        
        @JsonProperty("Width")
        private int width;
        
        @JsonProperty("Height")
        private int height;
        
        @JsonProperty("Dpi")
        private int dpi;
        
        // Getters and setters
        public int getEncryption() { return encryption; }
        public void setEncryption(int encryption) { this.encryption = encryption; }
        
        public int getFactor() { return factor; }
        public void setFactor(int factor) { this.factor = factor; }
        
        public FormatInfo getFormatInfo() { return formatInfo; }
        public void setFormatInfo(FormatInfo formatInfo) { this.formatInfo = formatInfo; }
        
        public int getPurpose() { return purpose; }
        public void setPurpose(int purpose) { this.purpose = purpose; }
        
        public int getQuality() { return quality; }
        public void setQuality(int quality) { this.quality = quality; }
        
        public int getType() { return type; }
        public void setType(int type) { this.type = type; }
        
        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }
        
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
        
        public int getDpi() { return dpi; }
        public void setDpi(int dpi) { this.dpi = dpi; }
    }
    
    public static class FormatInfo {
        @JsonProperty("FormatID")
        private int formatID;
        
        @JsonProperty("FormatOwner")
        private int formatOwner;
        
        // Getters and setters
        public int getFormatID() { return formatID; }
        public void setFormatID(int formatID) { this.formatID = formatID; }
        
        public int getFormatOwner() { return formatOwner; }
        public void setFormatOwner(int formatOwner) { this.formatOwner = formatOwner; }
    }
}