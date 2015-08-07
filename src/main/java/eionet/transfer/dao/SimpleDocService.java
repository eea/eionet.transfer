package eionet.transfer.dao;

import java.util.List;

import eionet.transfer.model.SimpleDoc;

//CRUD operations
public interface SimpleDocService {

    //Create
    //void save(SimpleDoc doc);

    //Read
    SimpleDoc getByName(String name);

    //Update
    //void update(SimpleDoc doc);

    //Delete
    //void deleteById(int id);

    //Get All
    List<SimpleDoc> getAll();
}
