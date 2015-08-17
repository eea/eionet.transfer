package eionet.transfer.dao;

import java.util.List;
import java.util.Date;

import eionet.transfer.model.Upload;

/**
 * Data for uploaded files.
 */
public interface UploadsService {

    /**
     * Save the meta data for an upload.
     */
    void save(Upload upload);

    /**
     * Fetch the meta data for one upload.
     */
    Upload getById(String id);

    /**
     * Delete file by Id.
     */
    void deleteById(String id);

    /**
     * Get a list of all files where the expiration date has passed.
     */
    List<String> getExpired();

    /**
     * Get a list of all files where the expiration date has passed.
     */
    List<String> getExpired(Date expirationDate);

    /**
     * Get all records.
     */
    List<Upload> getAll();

    /**
     * Delete all metadata for all uploads. Mainly used for testing.
     */
    void deleteAll();
}
