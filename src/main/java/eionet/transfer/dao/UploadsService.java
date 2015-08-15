package eionet.transfer.dao;

import java.util.List;

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
     * Delete all files where the expiration date has passed.
     */
    void deleteExpired();

    //Get All
    List<Upload> getAll();

    void deleteAll();
}
