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
import eionet.transfer.util.BreadCrumbs;
import eionet.transfer.util.Filenames;
import java.io.InputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 */
@Service
public class UploadsServiceDBFiles implements UploadsService {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private StorageService storageService;

    private Log logger = LogFactory.getLog(UploadsServiceDBFiles.class);

    public void storeFile(MultipartFile myFile, String uuidName, int fileTTL) throws IOException {
        storageService.save(myFile, uuidName);
        long now = System.currentTimeMillis();
        Date expirationDate = new Date(now + fileTTL * 3600L * 24L * 1000L);

        Upload rec = new Upload();
        rec.setId(uuidName);
        rec.setFilename(Filenames.removePath(myFile.getOriginalFilename()));
        rec.setContentType(myFile.getContentType());
        rec.setExpires(expirationDate);
        rec.setSize(myFile.getSize());
        String userName = getUserName();
        rec.setUploader(userName);
        metadataService.save(rec);
        logger.info("Uploaded: " + myFile.getOriginalFilename() + " by " + userName);
    }

    /**
     * Helper method to get authenticated userid.
     */
    private String getUserName() {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        //if (auth == null) {
        //    throw new IllegalArgumentException("Not authenticated");
        //}
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    /**
     * Download a file.
     */
    public Upload getById(String fileId) throws IOException {

        Upload uploadRec;
        try {
            uploadRec = metadataService.getById(fileId);
        } catch (Exception e) {
            throw new FileNotFoundException(fileId);
        }
        Date today = new Date(System.currentTimeMillis());
        if (today.after(uploadRec.getExpires())) {
            throw new FileNotFoundException(fileId);
        }
        uploadRec.setContentStream(storageService.getById(fileId));
        return uploadRec;
    }

    @Override
    public void deleteFiles(List<String> ids) throws IOException {
        for (String fileId : ids) {
            metadataService.deleteById(fileId);
            storageService.deleteById(fileId);
        }
    }

    @Override
    public List<String> getExpired() {
        return metadataService.getExpired();
    }

    @Override
    public List<Upload> getUnexpired() {
        return metadataService.getUnexpired();
    }

    @Override
    public long getFreeSpace() {
        return storageService.getFreeSpace();
    }

}

