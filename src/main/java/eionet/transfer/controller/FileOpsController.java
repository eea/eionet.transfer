package eionet.transfer.controller;

import eionet.transfer.util.BreadCrumbs;
import eionet.transfer.dao.StorageService;
import eionet.transfer.dao.UploadsService;
import eionet.transfer.model.Upload;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.sql.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * File operations - upload, download, delete.
 * See http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html
 * http://docs.oracle.com/javaee/6/tutorial/doc/gmhal.html
 */
@Controller
public class FileOpsController {

    @Autowired
    private UploadsService uploadsService;

    @Autowired
    private StorageService storageService;

    @RequestMapping(value = "/fileupload")
    public String fileUpload(Model model) {
        String pageTitle = "Upload file";
        BreadCrumbs.set(model, pageTitle);
        return "fileupload"; 
    } 

    /**
     * Helper method to get authenticated userid.
     */
    private String getUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    @RequestMapping(value = "/fileupload", method = RequestMethod.POST) 
    public String importFile(@RequestParam("file") MultipartFile myFile,
                        @RequestParam("fileTTL") int fileTTL,
                        final RedirectAttributes redirectAttributes) throws IOException { 
        //String pageTitle = "Upload file";

        //String dirFolder = System.getProperty("files.folder", "/var/tmp");
        //String uuidName = UUID.randomUUID().toString();
        String uuidName = storageService.save(myFile);
        redirectAttributes.addFlashAttribute("uuid", uuidName);
        long now = System.currentTimeMillis();
        Date expirationDate = new Date(now + fileTTL * 3600L * 1000L);

        Upload rec = new Upload();
        rec.setId(uuidName);
        rec.setFilename(myFile.getOriginalFilename());
        rec.setContentType(myFile.getContentType());
        rec.setExpires(expirationDate);
        rec.setSize(myFile.getSize());
        String userName = getUserName();
        rec.setUploader(userName);
        uploadsService.save(rec);
        // Redirect to a successful upload page 
        return "redirect:uploadSuccess"; 
    } 

    @RequestMapping(value = "/uploadSuccess")
    public String uploadResult(Model model, HttpServletRequest request) {
        String pageTitle = "File uploaded";
        BreadCrumbs.set(model, pageTitle);
        StringBuffer requestUrl = request.getRequestURL();
        model.addAttribute("url", requestUrl.substring(0, requestUrl.length() - "/uploadSuccess".length()));
        //if (model.containsAttribute("uuid")) {
        //    String uuid = (String) model.asMap().get("uuid");
        //    model.addAttribute("uuid", uuid); // Make it persistent for browser refresh
        //}
        return "uploadSuccess";
    }

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

    @RequestMapping(value = "/delete/{file_name}", method = RequestMethod.GET)
    public String deleteFile(
        @PathVariable("file_name") String fileId, HttpServletResponse response) throws IOException {
        Upload uploadRec = uploadsService.getById(fileId);
        uploadsService.deleteById(fileId);
        try {
            storageService.deleteById(fileId);
        } catch (IOException ex) {
        }
        return "deleteSuccess"; 
    }
}

