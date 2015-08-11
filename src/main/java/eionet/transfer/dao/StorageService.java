package eionet.transfer.dao;

import java.io.InputStream;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Store the file at a location and return a generated unique identifier for it.
     */
    String save(MultipartFile myFile) throws IOException;

    /**
     * Get an open stream to the stored object.
     */
    //InputStream getById(String id);

    //Delete
    void deleteById(String id);
}
