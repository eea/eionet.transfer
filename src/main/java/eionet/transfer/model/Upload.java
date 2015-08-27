package eionet.transfer.model;

import eionet.transfer.util.Humane;
import java.util.Date;

/**
 * Management and metadata for uploaded files. Contains original file name
 * expiration date etc.
 */
public class Upload {
	
    private String id;
    private Date expires;
    private String filename;
    private String uploader;
    private String contentType;
    private long fileSize;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return fileSize;
    }

    public void setSize(final long size) {
        this.fileSize = size;
    }

    public String getHumaneSize() {
        return Humane.humaneSize(fileSize);
    }
}
