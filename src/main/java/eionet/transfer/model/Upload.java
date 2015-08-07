package eionet.transfer.model;

import java.sql.Date;

public class Upload {
	
    private String id;
    private Date expires;
    private String filename;
    private String uploader;
    

    public Upload(String id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public Date getExpires() {
        return expires;
    }

    public String getFilename() {
        return filename;
    }

    public String getUploader() {
        return uploader;
    }
}
