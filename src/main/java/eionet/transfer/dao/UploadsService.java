package eionet.transfer.dao;

import java.util.List;
import java.util.Date;

import eionet.transfer.model.Upload;

/**
 * Service to store metadata for uploaded files.
 */
public interface UploadsService {

    /**
     * Save the metadata for an upload.
     */
    void save(Upload upload);

    /**
     * Fetch the metadata for one upload.
     */
    Upload getById(String id);

    /**
     * Delete metadata for file by Id.
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
