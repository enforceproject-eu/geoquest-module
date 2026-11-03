package org.n52.project.enforce.geoquest.utils;

public class ProvenanceEntry {

    private byte[] data;

    private String checksum;    

    public ProvenanceEntry(byte[] data, String checksum) {
        this.data = data;
        this.checksum = checksum;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
}
