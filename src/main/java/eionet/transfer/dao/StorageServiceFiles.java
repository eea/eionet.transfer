package eionet.transfer.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;


public class StorageServiceFiles implements StorageService {

    /**
     * The directory location where to store the uploaded files.
     */
    private String dirFolder;

    public void setDirFolder(String dirFolder) {
        this.dirFolder = dirFolder;
    }

    /**
     * Experimental method to show the user the max upload size.
     */
    public long getFreeSpace() {
        return new File(dirFolder).getFreeSpace();
    }

    @Override
    public String save(MultipartFile myFile) throws IOException {
        assert dirFolder != null;
        String uuidName = UUID.randomUUID().toString();
        File destination = new File(dirFolder, uuidName);
        myFile.transferTo(destination);
        return uuidName;
    }


    @Override
    public InputStream getById(String uuidName) throws IOException {
        File location = new File(dirFolder, uuidName);
        return new FileInputStream(location);
    }

    @Override
    public boolean deleteById(String uuidName) throws IOException {
        File location = new File(dirFolder, uuidName);
        return location.delete();
    }
}
