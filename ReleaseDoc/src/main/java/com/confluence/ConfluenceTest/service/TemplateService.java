package com.confluence.ConfluenceTest.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

@Service
public class TemplateService {

    private final String PROD_TEMPLATE_PATH = "templates/prod-release-note-template.html";
    private final String DEMO_TEMPLATE_PATH = "templates/demo-release-note-template.html";

    /**
     * Loads release note template from classpath.
     */
    public String loadTemplate(String env) throws Exception {
    	String fileName;

        if ("prod".equalsIgnoreCase(env)) {
            fileName = PROD_TEMPLATE_PATH;
        } else if ("demo".equalsIgnoreCase(env)) {
            fileName = DEMO_TEMPLATE_PATH;
        } else {
            throw new IllegalArgumentException("Invalid env. Allowed: prod | demo");
        }
        
        ClassPathResource resource = new ClassPathResource(fileName);
        try (InputStream in = resource.getInputStream();
             Scanner s = new Scanner(in, StandardCharsets.UTF_8.name())) {
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

    /**
     * Replace the deployment placeholder with provided HTML.
     */
    public String injectDeploymentTable(String templateHtml, String deploymentTableHtml) {
        if (templateHtml == null) templateHtml = "";
        if (deploymentTableHtml == null) deploymentTableHtml = "";
        return templateHtml.replace("{{DYNAMIC_TABLE_DEPLOYMENT}}", deploymentTableHtml);
    }
    
	public String applyDynamicTables(String template, Map<String, String> tableHtmlMap) {

		String finalHtml = template;

		for (String placeholder : tableHtmlMap.keySet()) {
			finalHtml = finalHtml.replace("{{" + placeholder + "}}", tableHtmlMap.get(placeholder));
		}

		return finalHtml;
	}

}
