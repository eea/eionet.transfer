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
package eionet.transfer.controller;

import eionet.transfer.dao.StorageService;
import eionet.transfer.dao.UploadsService;
import eionet.transfer.model.Upload;
import eionet.transfer.util.BreadCrumbs;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    private Log logger = LogFactory.getLog(FileOpsController.class);

    /**
     * Form for uploading a file.
     */
    @RequestMapping(value = "/fileupload")
    public String fileUpload(Model model) {
        String pageTitle = "Transfer file";
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        return "fileupload";
    }

    /**
     * Upload file for transfer.
     */
    @RequestMapping(value = "/fileupload", method = RequestMethod.POST)
    public String importFile(@RequestParam("file") MultipartFile myFile,
                        @RequestParam("fileTTL") int fileTTL,
                        final RedirectAttributes redirectAttributes,
                        final HttpServletRequest request) throws IOException {

        if (myFile == null || myFile.getOriginalFilename() == null) {
            redirectAttributes.addFlashAttribute("message", "Select a file to upload");
            return "redirect:fileupload";
        }
        if (fileTTL > 90) {
            redirectAttributes.addFlashAttribute("message", "Invalid expiration date");
            return "redirect:fileupload";
        }
        String uuidName = storeFile(myFile, fileTTL);
        redirectAttributes.addFlashAttribute("uuid", uuidName);
        StringBuffer requestUrl = request.getRequestURL();
        redirectAttributes.addFlashAttribute("url", requestUrl.substring(0, requestUrl.length() - "/fileupload".length()));
        return "redirect:fileupload";
        //return "redirect:uploadSuccess";
    }

    /**
     * AJAX Upload file for transfer.
     */
    @RequestMapping(value = "/fileupload", method = RequestMethod.POST, params="ajaxupload=1")
    public void importFileWithAJAX(@RequestParam("file") MultipartFile myFile,
                        @RequestParam("fileTTL") int fileTTL,
                        HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (myFile == null || myFile.getOriginalFilename() == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Select a file to upload");
            return;
        }
        if (fileTTL > 90) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invalid expiration date");
            return;
        }
        String uuidName = storeFile(myFile, fileTTL);
        response.setContentType("text/xml");
        PrintWriter printer = response.getWriter();
        StringBuffer requestUrl = request.getRequestURL();
        String url = requestUrl.substring(0, requestUrl.length() - "/fileupload".length());
        printer.println("<?xml version='1.0'?>");
        printer.println("<package>");
        printer.println("<downloadLink>" + url + "/download/" + uuidName + "</downloadLink>");
        printer.println("<deleteLink>" + url + "/delete/" + uuidName + "</deleteLink>");
        printer.println("</package>");
        printer.flush();
        response.flushBuffer();
    }

    private String storeFile(MultipartFile myFile, int fileTTL) throws IOException {
        String uuidName = storageService.save(myFile);
        long now = System.currentTimeMillis();
        Date expirationDate = new Date(now + fileTTL * 3600L * 24L * 1000L);

        Upload rec = new Upload();
        rec.setId(uuidName);
        rec.setFilename(myFile.getOriginalFilename());
        rec.setContentType(myFile.getContentType());
        rec.setExpires(expirationDate);
        rec.setSize(myFile.getSize());
        String userName = getUserName();
        rec.setUploader(userName);
        uploadsService.save(rec);
        logger.info("Uploaded: " + myFile.getOriginalFilename() + " by " + userName);
        return uuidName;
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
     * Page to show upload success.
     */
    /*
    @RequestMapping(value = "/uploadSuccess")
    public String uploadResult(Model model, HttpServletRequest request) {
        String pageTitle = "File uploaded";
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        StringBuffer requestUrl = request.getRequestURL();
        model.addAttribute("url", requestUrl.substring(0, requestUrl.length() - "/uploadSuccess".length()));
        return "uploadSuccess";
    }
    */

    /**
     * Download a file.
     */
    @RequestMapping(value = "/download/{file_name}", method = RequestMethod.GET)
    public void downloadFile(
        @PathVariable("file_name") String fileId, HttpServletResponse response) throws IOException {
        Upload uploadRec = uploadsService.getById(fileId);
        Date today = new Date(System.currentTimeMillis());
        if (today.after(uploadRec.getExpires())) {
            throw new IOException("File not found");
        }
        InputStream is = null;
        is = storageService.getById(fileId);
        response.setContentType("application/octet-stream");
        //response.setContentLength(uploadRec.getSize()); // Too small - max 2.1 GB.
        response.setHeader("Content-Length", Long.toString(uploadRec.getSize()));
        response.setHeader("Content-Disposition", "attachment; filename=" + uploadRec.getFilename());

        org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
        response.flushBuffer();
        is.close();
        deleteExpired();
    }

    /**
     * Delete all expired items from the database.
     */
    private void deleteExpired() {
        try {
            List<String> expiredObjects = uploadsService.getExpired();
            for (String fileId : expiredObjects) {
                uploadsService.deleteById(fileId);
                storageService.deleteById(fileId);
            }
        } catch (IOException ex) {
            logger.error("I/O exception when deleting expired files");
        }

    }

    @RequestMapping(value = "/delete/{file_name}")
    public String deleteFile(
        @PathVariable("file_name") String fileId, final RedirectAttributes redirectAttributes) throws IOException {
        uploadsService.deleteById(fileId);
        storageService.deleteById(fileId);
        redirectAttributes.addFlashAttribute("message", "File deleted");
        return "redirect:/";
    }
}

