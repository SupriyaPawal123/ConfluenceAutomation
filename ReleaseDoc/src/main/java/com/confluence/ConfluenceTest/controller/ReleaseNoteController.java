package com.confluence.ConfluenceTest.controller;

import com.confluence.ConfluenceTest.service.ExcelService;
import com.confluence.ConfluenceTest.service.TemplateService;
import com.confluence.ConfluenceTest.util.HtmlGeneratorUtil;
import com.confluence.ConfluenceTest.util.HtmlTableGenerator;
import com.example.confluence.ConfluenceReleaseDocGenerator; // your existing class
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ReleaseNoteController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private TemplateService templateService;

    /**
     * Upload Excel, replace deployment table in the template, create confluence page.
     *
     * Expects Excel sheet name: "Deployment"
     */
    @PostMapping("/upload-deployment")
    public String uploadDeploymentAndCreatePage(@RequestParam("file") MultipartFile file,@RequestParam("env") String env) throws Exception {

        // Excel -> Map<sheetName, List<rowMap>>
        Map<String, List<Map<String, String>>> excelJson = excelService.formatExcelData(file);
        System.out.println("*****"+excelJson.toString());
        
        
     // Step 2: Convert JSON → HTML
//        String html = HtmlGeneratorUtil.generateHtmlFromJson(excelJson);

        // Step 3: Create Confluence Page
//        ConfluenceReleaseDocGenerator.createNewPage("Test21 Auto Release Note1", html);

        // Get "Deployment" sheet (case-sensitive). If absent, try uppercase/lowercase fallback.
        List<Map<String, String>> deploymentRows = excelJson.values().stream().flatMap(List::stream).collect(Collectors.toList());
        		
//        		excelJson.get("Basic_Details");
        if (deploymentRows == null) {
            // try common fallbacks
            deploymentRows = excelJson.get("basic_Details");
        }
        if (deploymentRows == null) {
            return "ERROR: Sheet 'Deployment' not found in uploaded Excel. Sheets found: " + excelJson.keySet();
        }

        // Convert deployment sheet -> HTML table
        String deploymentTableHtml = HtmlTableGenerator.generateTableHtml(deploymentRows);
        String html = HtmlGeneratorUtil.generateHtmlFromJson(excelJson);

        // Load template and inject table
        String templateHtml = templateService.loadTemplate(env);
        String finalHtml = templateService.injectDeploymentTable(templateHtml, html);

        // Create Confluence page
        // Title can be customized; using timestamp is helpful
        String title = "PROD Release Note - Auto (" + System.currentTimeMillis() + ")";
        ConfluenceReleaseDocGenerator.createNewPageWithoutParent(title, finalHtml );

        return "Success: Page created with deployment table.";
    }
    
	@PostMapping("/upload-release-details")
	public String uploadExcel(@RequestParam("file") MultipartFile file,@RequestParam("env") String env,@RequestParam("parentPageName") String parentPageName ) throws Exception {

		Map<String, List<Map<String, String>>> excelJson = excelService.formatExcelData(file);

		 // 1. Lookup parent page ID by name
	    String parentPageId = ConfluenceReleaseDocGenerator.getPageIdByTitle(parentPageName);

		//2.  Load template
		String templateHtml = templateService.loadTemplate(env);

		Map<String, String> tableHtmlMap = new HashMap();

		// 1. Basic Details → {{TABLE_BASIC_DETAILS}}
		if (excelJson.containsKey("Basic_Details")) {
			tableHtmlMap.put("TABLE_BASIC_DETAILS",
					HtmlTableGenerator.generateTableHtml(excelJson.get("Basic_Details")));
		}

		// 2. Story Details → {{TABLE_STORY_DETAILS}}
		if (excelJson.containsKey("Story_Details")) {
			tableHtmlMap.put("TABLE_STORY_DETAILS",
					HtmlTableGenerator.generateTableHtml(excelJson.get("Story_Details")));
		}

		// 3. Key Contacts → {{TABLE_KEY_CONTACTS}}
		if (excelJson.containsKey("Key_Contacts")) {
			tableHtmlMap.put("TABLE_KEY_CONTACTS", HtmlTableGenerator.generateTableHtml(excelJson.get("Key_Contacts")));
		}
		
		// 4. Key Contacts → {{TABLE_TOLLGATE_PROCESS_CHECK}}
				if (excelJson.containsKey("Tollgate_Process_Check")) {
					tableHtmlMap.put("TABLE_TOLLGATE_PROCESS_CHECK", HtmlTableGenerator.generateTableHtml(excelJson.get("Tollgate_Process_Check")));
				} 
				

		// 5. Lower Environment Testing → {{TABLE_RISK_AWARENESS}}
		if (excelJson.containsKey("Risk_Awareness")) {
		tableHtmlMap.put("TABLE_RISK_AWARENESS",
		HtmlTableGenerator.generateTableHtml(excelJson.get("Risk_Awareness")));
		}
				
		// 6. Lower Environment Testing → {{TABLE_LOWER_ENV_TESTING}}
		if (excelJson.containsKey("Lower_Environment_Testing")) {
			tableHtmlMap.put("TABLE_LOWER_ENV_TESTING",
					HtmlTableGenerator.generateTableHtml(excelJson.get("Lower_Environment_Testing")));
		}

		// 7. Deployment Components → {{TABLE_PRE_IMPLEMENTATION_STEPS}}
		if (excelJson.containsKey("Pre_Implementation_Steps")) {
			tableHtmlMap.put("TABLE_PRE_IMPLEMENTATION_STEPS", HtmlTableGenerator.generateTableHtml(excelJson.get("Pre_Implementation_Steps")));
		}

		// 8. Deployment Components → {{TABLE_AUTOMATE_IMPLEMENTATION_STEPS}}
		if (excelJson.containsKey("Automate_Implementation_Steps")) {
			tableHtmlMap.put("TABLE_AUTOMATE_IMPLEMENTATION_STEPS", HtmlTableGenerator.generateTableHtml(excelJson.get("Automate_Implementation_Steps")));
		}
		// 9. Deployment Components → {{TABLE_INSTALL_IMPLEMENTATION_STEPS}}
		if (excelJson.containsKey("Install_Implementation_Steps")) {
			tableHtmlMap.put("TABLE_INSTALL_IMPLEMENTATION_STEPS", HtmlTableGenerator.generateTableHtml(excelJson.get("Install_Implementation_Steps")));
		}
		
		// 10. Deployment Components → {{TABLE_AUTOMATED_VALIDATION_STEPS}}
		if (excelJson.containsKey("Automated_Validation_Steps")) {
			tableHtmlMap.put("TABLE_AUTOMATED_VALIDATION_STEPS", HtmlTableGenerator.generateTableHtml(excelJson.get("Automated_Validation_Steps")));
		}

		// 11. Deployment Components → {{TABLE_VALIDATION_STEPS}}
		if (excelJson.containsKey("Validation_Steps")) {
			tableHtmlMap.put("TABLE_VALIDATION_STEPS", HtmlTableGenerator.generateTableHtml(excelJson.get("Validation_Steps")));
		}
		// 12. Deployment Components → {{TABLE_AUTOMATED_BACK_OUT_STEPS}}
		if (excelJson.containsKey("Automated_Back_out_Steps ")) {
			tableHtmlMap.put("TABLE_AUTOMATED_BACK_OUT_STEPS", HtmlTableGenerator.generateTableHtml(excelJson.get("Automated_Back_out_Steps ")));
		}

		// 13. Deployment Components → {{TABLE_BACKOUT_STEPS}}
		if (excelJson.containsKey("Back_out_Steps")) {
			tableHtmlMap.put("TABLE_BACKOUT_STEPS", HtmlTableGenerator.generateTableHtml(excelJson.get("Back_out_Steps")));
		}
		
		// 14. Deployment Components → {{TABLE_BACKOUT_STEPS_VALIDATIONS}}
		if (excelJson.containsKey("Backout_Steps_Validations")) {
			tableHtmlMap.put("TABLE_BACKOUT_STEPS_VALIDATIONS",
					HtmlTableGenerator.generateTableHtml(excelJson.get("Backout_Steps_Validations")));
		}
		
		// 14. Deployment Components → {{TABLE_POST_IMPLEMENTATION_STEPS}}
		if (excelJson.containsKey("Post_Implementation_Steps")) {
			tableHtmlMap.put("TABLE_POST_IMPLEMENTATION_STEPS", HtmlTableGenerator.generateTableHtml(excelJson.get("Post_Implementation_Steps")));
		}

		// Inject everything into template
		String finalHtml = templateService.applyDynamicTables(templateHtml, tableHtmlMap);

		// Upload to Confluence
		ConfluenceReleaseDocGenerator.createNewPage(env+" Release Note " + System.currentTimeMillis(), finalHtml, parentPageId);

		return " "+env+" Confluence release note created with dynamic tables.";
	}
}
