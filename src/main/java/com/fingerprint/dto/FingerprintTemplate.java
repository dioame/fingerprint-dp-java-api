package com.fingerprint.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "FingerprintTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class FingerprintTemplate {
    @XmlElement(name = "Format")
    private String format;
    
    @XmlElement(name = "Version")
    private String version;
    
    @XmlElement(name = "Data")
    private String data;
    
    // Getters and setters
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
