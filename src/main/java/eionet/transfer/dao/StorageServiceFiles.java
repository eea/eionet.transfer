package eionet.transfer.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service to store files in the file system.
 */
public class StorageServiceFiles implements StorageService {

    /**
     * The directory location where to store the uploaded files.
     */
    private String storageDir;

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    /**
     * Experimental method to show the user the max upload size.
     */
    public long getFreeSpace() {
        return new File(storageDir).getFreeSpace();
    }

    @Override
    public String save(MultipartFile myFile) throws IOException {
        assert storageDir != null;
        String uuidName = UUID.randomUUID().toString();
        File destination = new File(storageDir, uuidName);
        myFile.transferTo(destination);
        return uuidName;
    }


    @Override
    public InputStream getById(String uuidName) throws IOException {
        File location = new File(storageDir, uuidName);
        return new FileInputStream(location);
    }

    @Override
    public boolean deleteById(String uuidName) throws IOException {
        File location = new File(storageDir, uuidName);
        return location.delete();
    }
}
