package eionet.transfer.controller;

import eionet.transfer.util.BreadCrumbs;
import eionet.transfer.dao.StorageService;
import eionet.transfer.dao.UploadsService;
import eionet.transfer.model.Upload;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.sql.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Download a file
 */
@Controller
public class FileDownloadController {

    @Autowired
    private UploadsService uploadsService;

    @Autowired
    private StorageService storageService;

    /**
     * TODO: Check that it is before the purge date.
     *       Clean up old files.
     */
    @RequestMapping(value = "/download/{file_name}", method = RequestMethod.GET)
    public void getFile(
        @PathVariable("file_name") String fileId, HttpServletResponse response) throws IOException {
        Upload uploadRec = uploadsService.getById(fileId);
        InputStream is = null;
        try {
          is = storageService.getById(fileId);
          response.setContentType("application/octet-stream");
          //response.setContentLength(uploadRec.getSize()); // Too small - max 2.1 GB.
          response.setHeader("Content-Length", Long.toString(uploadRec.getSize()));
          response.setHeader("Content-Disposition", "attachment; filename=" + uploadRec.getFilename());
          // copy it to response's OutputStream
          org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
          response.flushBuffer();
        } catch (IOException ex) {
          //log.info("Error writing file to output stream. Filename was '{}'", fileId, ex);
          throw new RuntimeException("IOError writing file to output stream");
        }
        if (is != null) {
            is.close();
        }
    }
}
