package com.example.confluence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfluenceReleaseDocGenerator {

    private static final String BASE_URL = "https://pawalsupriya001.atlassian.net/wiki/rest/api";
    private static final String USER_EMAIL = "pawalsupriya001@gmail.com";
    private static final String API_TOKEN = "ATATT3xFfGF0Ubmf74dxiZ9wRFHTfAVXv66hwrGue6LK8w8dfBXTa6gDJFE10DSpINUDDC6say_ZFOb_Qzo128CcaueC1kT86fukg_XpJSJaxsR2MGMojRFjuvUpsVDcqit1Fk4cPQHTp_qJ3ivGlqYSnVzutQiIOnsdt_2GtHZWKgnbKOsKkoo=2C3863F5";    private static final String SPACE_KEY = "MFS";

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

	
	public static void createNewPageWithoutParent(String title, String htmlContent) throws Exception {

		ObjectNode storage = mapper.createObjectNode();
		storage.put("value", htmlContent);
		storage.put("representation", "storage");

		ObjectNode body = mapper.createObjectNode();
		body.set("storage", storage);

		ObjectNode payload = mapper.createObjectNode();
		payload.put("type", "page");
		payload.put("title", title);

		ObjectNode space = mapper.createObjectNode();
		space.put("key", SPACE_KEY);
		payload.set("space", space);

		payload.set("body", body);

		HttpRequest request = HttpRequest.newBuilder().uri(new URI(BASE_URL + "/content"))
				.header("Authorization", getAuthHeader()).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(payload.toString())).build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() >= 200 && response.statusCode() < 300) {
			System.out.println("Page Created Successfully!");
			System.out.println(response.body());
		} else {
			throw new RuntimeException("Failed: " + response.statusCode() + " - " + response.body());
		}
	}
    
    public static void createNewPage(String title, String storageValue, String parentPageId) throws Exception {

        // Storage format content
        ObjectNode storageNode = mapper.createObjectNode();
        storageNode.put("value", storageValue);
        storageNode.put("representation", "storage");

        // Body object
        ObjectNode bodyNode = mapper.createObjectNode();
        bodyNode.set("storage", storageNode);

        // Parent (ancestor)
        ArrayNode ancestorsArray = mapper.createArrayNode();
        ObjectNode parentNode = mapper.createObjectNode();
        parentNode.put("id", parentPageId);
        ancestorsArray.add(parentNode);

        // Main payload
        ObjectNode payload = mapper.createObjectNode();
        payload.put("type", "page");
        payload.put("title", title);
        payload.set("space", mapper.createObjectNode().put("key", SPACE_KEY));
        payload.set("ancestors", mapper.createArrayNode()
                .add(mapper.createObjectNode().put("id", parentPageId)));
        payload.set("body", bodyNode);

        String jsonPayload = payload.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/content"))
                .header("Authorization", getAuthHeader())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("Confluence Page Created Successfully:");
            System.out.println(response.body());
        } else {
            System.err.println("Failed to create page:");
            System.err.println("Status: " + response.statusCode());
            System.err.println("Response: " + response.body());
            throw new RuntimeException("Page creation failed");
        }
    }


    private static String getAuthHeader() {
        String auth = USER_EMAIL + ":" + API_TOKEN;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }
    
    public static String getPageIdByTitle(String pageTitle) throws Exception {
        String uri = BASE_URL + "/content?title=" + 
                     URLEncoder.encode(pageTitle, StandardCharsets.UTF_8) +
                     "&spaceKey=" + SPACE_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Authorization", getAuthHeader())
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to search page by title. Status: " + response.statusCode());
        }

        JsonNode root = mapper.readTree(response.body());
        JsonNode results = root.path("results");

        if (results.isArray() && results.size() > 0) {
            return results.get(0).path("id").asText();
        }

        throw new RuntimeException("No Confluence page found with title: " + pageTitle);
    }
}
