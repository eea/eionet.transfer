package eionet.transfer.dao;

import java.util.List;
import java.sql.Date;

import eionet.transfer.model.Upload;

//CRUD operations
public interface UploadsService {

    //Create
    void save(Upload upload);

    //Read
    Upload getById(String id);

    //Update
    //void update(Upload upload);

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

    void deleteAll();
}
