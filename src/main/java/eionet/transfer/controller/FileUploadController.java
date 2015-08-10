package eionet.transfer.controller;

import eionet.transfer.util.BreadCrumbs;
import eionet.transfer.dao.UploadsService;
import eionet.transfer.model.Upload;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


/**
 * See http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html
 * http://docs.oracle.com/javaee/6/tutorial/doc/gmhal.html
 */
@Controller
public class FileUploadController {

    @Autowired
    private UploadsService uploadsService;

    @RequestMapping(value = "/fileupload")
    public String fileUpload(Model model) {
        String pageTitle = "Upload file";
        BreadCrumbs.set(model, pageTitle);
        return "fileupload"; 
    } 

    @RequestMapping(value = "/fileupload", method = RequestMethod.POST) 
    public String importFile(@RequestParam("file") MultipartFile myFile, @RequestParam("fileTTL") int fileTTL) throws IOException { 
        String pageTitle = "Upload file";

        String dirFolder = System.getProperty("files.folder", "/var/tmp");
        String uuidName = UUID.randomUUID().toString();
        File destination = new File(dirFolder, uuidName);
        myFile.transferTo(destination);
        Upload rec = new Upload(uuidName, "testfile.txt");
        uploadsService.save(rec);
        // Redirect to a successful upload page 
        return "redirect:uploadSuccess"; 
    } 

    @RequestMapping(value = "/uploadSuccess")
    public String uploadResult(Model model) {
        String pageTitle = "File uploaded";
        BreadCrumbs.set(model, pageTitle);
        return "uploadSuccess";
    }
}
