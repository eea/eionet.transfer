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

    //Delete
    //public void deleteByCode(String code);

    //Get All
    List<Upload> getAll();
}
