package eionet.transfer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import eionet.transfer.dao.UploadsService;
import eionet.transfer.model.Upload;
import eionet.transfer.util.BreadCrumbs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UploadsController {

    @Autowired
    private UploadsService uploadsService;

    @RequestMapping(value="/uploads", method=RequestMethod.GET)
    public String findUploadsThymeLeaf(Model model){
        String pageTitle = "Uploads List";

        List<Upload> uploads = uploadsService.getAll();
        model.addAttribute("uploads", uploads);
        model.addAttribute("title", pageTitle);
        BreadCrumbs.set(model, pageTitle);
        return "uploads";
    }

}
