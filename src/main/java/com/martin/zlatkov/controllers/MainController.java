package com.martin.zlatkov.controllers;

    import com.martin.zlatkov.helpers.ReadFileFromSource;
import com.martin.zlatkov.models.Employee;
import com.martin.zlatkov.models.FileDB;
import com.martin.zlatkov.models.Pair;
import com.martin.zlatkov.models.WorkTogether;
import com.martin.zlatkov.repositories.PairRepository;
import com.martin.zlatkov.services.FileDBService;
import com.martin.zlatkov.services.PairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;

@Controller
public class MainController {

    private PairRepository pairRepository;
    private ReadFileFromSource readFileFromSource;
    private FileDBService fileDBService;
    private PairService pairService;

    @Autowired
    public MainController(PairService pairService, PairRepository pairRepository, ReadFileFromSource readFileFromSource, FileDBService fileDBService) {
        this.pairRepository = pairRepository;
        this.readFileFromSource = readFileFromSource;
        this.fileDBService = fileDBService;
        this.pairService=pairService;
    }

    @GetMapping("/uploadPages")
    public String getUploadPage() throws IOException {
        System.out.println("Upload Page found!");
        //readFileFromSource.calculatePair(t);
        return "uploadPage";
    }

    /**
     * Method for uploading file from the user to DB
     *
     * @param file
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST, params = {"upload"})
    public ModelAndView insertFileIntoResources(@RequestParam("file") MultipartFile file, Model model, HttpServletRequest request) throws IOException, ServletException {
//        /* Receive file uploaded to the Servlet from the uploadPage form */
//        Part filePart = request.getPart("file");
//        String fileName = filePart.getSubmittedFileName();
//        for (Part part : request.getParts()) {
//            part.write("C:\\Users\\" + fileName);
//        }
//        response.getWriter().print("The file uploaded sucessfully.");
//        String message = "";
        ModelAndView mav = new ModelAndView("uploadPage");
        ModelAndView mav2 = new ModelAndView("calcPage");
        try {
            String fName = file.getName();
            Part filePart = request.getPart("file");
            String fileName = filePart.getSubmittedFileName();
            //fileDBService.findByName(fileName);
            if (fileDBService.findByName(fileName) == null) {
                fileDBService.store(file);
                model.addAttribute("fileNames", fileDBService.getAll());
                return mav2;
            } else {
                model.addAttribute("message", "We have already uploaded this file in DB!");
            }
//            message = "Uploaded the file successfully to database: " + file.getOriginalFilename();
//            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
//            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }

        return mav;
    }

    @RequestMapping("/calcFile")
    public ModelAndView getFile(@RequestParam(value = "fileChoosen") String name, Model model) throws IOException {
        ModelAndView mav = new ModelAndView("result");
        //System.out.println(name);

        //Get a file from DB by id
        FileDB fileDB = fileDBService.getFile(name);

        byte[] ll = fileDB.getData();

        //We read file from resource folder
        String nameM = fileDB.getName();
        Pair n = pairService.findExistingPair(nameM);
        if(n == null) {
            List<Employee> t = readFileFromSource.readFile(fileDB);
            List<WorkTogether> list = readFileFromSource.calculatePair(t);

            Pair nn = new Pair();
            for (int i = 0; i < list.size(); i++) {
                nn.setEmp1(list.get(i).ID1);
                nn.setEmp2(list.get(i).ID2);
                nn.setProjectID(list.get(i).projectID);
                nn.setTotalDaysWorkedTogether((int) list.get(i).daysTotalTogether);
                nn.setFileDBName(fileDB.getName());
                pairRepository.save(nn);
            }
        }

        model.addAttribute("pairs", pairRepository.findAll());
        return mav;
    }

    @GetMapping("/showCalcPage")
    public String showCalcPage(Model model) {
        model.addAttribute("fileNames", fileDBService.getAll());
        return "calcPage";
    }

}
