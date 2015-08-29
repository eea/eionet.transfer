package eionet.transfer.controller;

import java.util.List;

import eionet.transfer.dao.UploadsService;
import eionet.transfer.model.Upload;
import eionet.transfer.util.BreadCrumbs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * View uploaded files.
 */
@Controller
public class UploadsController {

    @Autowired
    private UploadsService uploadsService;

    @RequestMapping(value = "/uploads", method = RequestMethod.GET)
    public String findUploads(Model model) {
        String pageTitle = "Uploads List";

        List<Upload> uploads = uploadsService.getUnexpired();
        model.addAttribute("uploads", uploads);
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        return "uploads";
    }

}
