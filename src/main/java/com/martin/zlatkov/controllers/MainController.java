package com.martin.zlatkov.controllers;

import com.martin.zlatkov.helpers.ReadFileFromSource;
import com.martin.zlatkov.models.Employee;
import com.martin.zlatkov.models.Pair;
import com.martin.zlatkov.models.WorkTogether;
import com.martin.zlatkov.repositories.PairRepository;
import com.martin.zlatkov.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private ServletContext servletContext;
    @Autowired
    private PairRepository pairRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ReadFileFromSource readFileFromSource;

    @GetMapping("/uploadPages")
    public String getUploadPage() throws IOException {
        System.out.println("Upload Page found!");
        //readFileFromSource.calculatePair(t);
        return "uploadPage";
    }

    @GetMapping("/results")
    public ModelAndView showResults(Model model) throws IOException {
        ModelAndView modelAndView = new ModelAndView("result");
        //We read file from resource folder
        List<Employee> t = readFileFromSource.readFile();
        List<WorkTogether> list = readFileFromSource.calculatePair(t);

        Pair nn = new Pair();
        for (int i = 0; i < list.size(); i++) {
            nn.setEmp1(list.get(i).ID1);
            nn.setEmp2(list.get(i).ID2);
            nn.setProjectID(list.get(i).projectID);
            nn.setTotalDaysWorkedTogether((int) list.get(i).daysTotalTogether);
            pairRepository.save(nn);
        }
        model.addAttribute("pairs", pairRepository.findAll());
        return modelAndView;
    }


//    @RequestMapping(value ="/uploadFile", method = RequestMethod.POST)
//    public String insertFileIntoResources(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        /* Receive file uploaded to the Servlet from the uploadPage form */
//        Part filePart = request.getPart("file");
//        String fileName = filePart.getSubmittedFileName();
//        for (Part part : request.getParts()) {
//            part.write("C:\\Users\\art\\Downloads\\tests\\" + fileName);
//        }
//        response.getWriter().print("The file uploaded sucessfully.");
//        return "result";
//    }

//    @GetMapping("/uploadedFiles")
//    public String listUploadedFiles(Model model) throws IOException {
//
//        model.addAttribute("files", storageService
//                .loadAll()
//                .map(path ->
//                        MvcUriComponentsBuilder
//                                .fromMethodName(MainController.class, "serveFile", path.getFileName().toString())
//                                .build().toString())
//                .collect(Collectors.toList()));
//
//        return "uploadForm";
        //}



    }