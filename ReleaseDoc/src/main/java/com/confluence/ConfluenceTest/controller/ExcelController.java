package com.confluence.ConfluenceTest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.confluence.ConfluenceTest.service.ExcelService;
import com.confluence.ConfluenceTest.util.HtmlGeneratorUtil;
import com.example.confluence.ConfluenceReleaseDocGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/upload-excel")
    public String uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {

        // Step 1: Convert Excel → JSON
        Map<String, List<Map<String, String>>> json = excelService.formatExcelData(file);

        // Step 2: Convert JSON → HTML
        String html = HtmlGeneratorUtil.generateHtmlFromJson(json);

        // Step 3: Create Confluence Page
        ConfluenceReleaseDocGenerator.createNewPageWithoutParent("Test21 Auto Release Note1", html);

        return "Uploaded & Page Created!";
    }
}