/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Transfer 1.0
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. All Rights Reserved.
 *
 * Contributor(s):
 *        Søren Roug
 */
package eionet.transfer.dao;

import eionet.transfer.model.Upload;
import eionet.transfer.controller.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service to store files in the Swift Object Store.
 */
@Service
public class UploadsServiceSwift implements UploadsService {

    /**
     * The swift location where to store the uploaded files.
     */
    @Value("${swift.authurl}")
    private String swiftAuthUrl;

    @Value("${swift.tenantid}")
    private String swiftTenantId;

    @Value("${swift.username}")
    private String swiftUsername;

    @Value("${swift.password}")
    private String swiftPassword;

    @Value("${swift.mock}")
    private String swiftMock;

    private AccountConfig config;

    private Account account;

    private Container container;

    private Log logger = LogFactory.getLog(UploadsServiceSwift.class);

    /**
     * Constructor.
     */
    public UploadsServiceSwift() {
        if (swiftUsername != null && swiftPassword != null && swiftAuthUrl != null) {
            login();
        } else {
            logger.info("Swift storage account is not configured");
        }
    }

    /**
     * Logs into the Object store, but note that the token only works for 24 hours.
     * FIXME: Test
     */
    private void login() {
        config = new AccountConfig();
        config.setUsername(swiftUsername);
        config.setPassword(swiftPassword);
        config.setAuthUrl(swiftAuthUrl);
        config.setTenantId(swiftTenantId);
//      config.setTenantName(swiftTenantName);
        config.setMock(true);
        account = new AccountFactory(config).createAccount();
        container = account.getContainer("transfer");
        if (!container.exists()) {
            container.create();
        }
        //container.makePublic();
    }

// https://github.com/javaswift/joss/blob/master/src/main/java/org/javaswift/joss/model/StoredObject.java
    @Override
    public void storeFile(MultipartFile myFile, String fileId, int fileTTL) throws IOException {
        assert swiftUsername != null;
        StoredObject swiftObject = container.getObject(fileId);
        swiftObject.setContentType(myFile.getContentType());
        swiftObject.uploadObject(myFile.getInputStream());

        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("filename", myFile.getOriginalFilename());
        metadata.put("content-type", myFile.getContentType());
        swiftObject.setMetadata(metadata);
        swiftObject.saveMetadata();
        //swiftObject.setDeleteAt(Date date);
    }


    @Override
    public Upload getById(String fileId) throws IOException {
        Upload uploadRec = new Upload();
        StoredObject swiftObject = null;
        try {
            swiftObject = container.getObject(fileId);
        } catch (Exception e) {
            throw new FileNotFoundException(fileId);
        }
        Map<String, Object> metadata = swiftObject.getMetadata();
        uploadRec.setExpires(swiftObject.getDeleteAtAsDate());
        uploadRec.setFilename((String) metadata.get("filename"));
        uploadRec.setContentType((String) metadata.get("content-type"));
        uploadRec.setSize(swiftObject.getContentLength());
        Date today = new Date(System.currentTimeMillis());
        if (today.after(uploadRec.getExpires())) {
            throw new FileNotFoundException(fileId);
        }

        uploadRec.setContentStream(swiftObject.downloadObjectAsInputStream());
        return uploadRec;
    }

    @Override
    public void deleteFiles(List<String> ids) throws IOException {
        for (String fileId : ids) {
            StoredObject swiftObject = container.getObject(fileId);
            swiftObject.delete();
        }
    }

    @Override
    public List<Upload> getUnexpired() {
        //FIXME
        return new ArrayList<Upload>(0);
    }

    /**
     * The object store can expire files automatically. We don't have to do it.
     */
    @Override
    public List<String> getExpired() {
        return new ArrayList<String>(0);
    }

    @Override
    public long getFreeSpace() {
        return 0L;
    }
}
