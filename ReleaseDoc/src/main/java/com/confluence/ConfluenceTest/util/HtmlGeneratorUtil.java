package com.confluence.ConfluenceTest.util;

import java.util.List;
import java.util.Map;

public class HtmlGeneratorUtil {

    public static String generateHtmlFromJson(Map<String, List<Map<String, String>>> excelJson) {

        StringBuilder html = new StringBuilder();

        html.append("<html><head>")
            .append("<meta charset='UTF-8' />")
            .append("<style>")
            .append("table { border-collapse: collapse; width:100%; margin-bottom:20px; }")
            .append("th, td { border:1px solid #777; padding:6px; white-space:nowrap; }")
            .append("th { background:#f0f0f0; font-weight:bold; }")
            .append("h2 { margin-top:30px; font-size:18px; }")
            .append("</style>")
            .append("</head><body>");

        for (String sheetName : excelJson.keySet()) {

            html.append("<h2>").append(sheetName).append("</h2>");

            List<Map<String, String>> rows = excelJson.get(sheetName);

            if (rows == null || rows.isEmpty()) {
                html.append("<p>No data found</p>");
                continue;
            }

            html.append("<table><tr>");

            // headers from first row
            Map<String, String> firstRow = rows.get(0);

            for (String header : firstRow.keySet()) {
                html.append("<th>").append(header).append("</th>");
            }

            html.append("</tr>");

            // data rows
            for (Map<String, String> row : rows) {
                html.append("<tr>");
                for (String header : firstRow.keySet()) {
                    html.append("<td>").append(row.getOrDefault(header, "")).append("</td>");
                }
                html.append("</tr>");
            }

            html.append("</table>");
        }

        html.append("</body></html>");

        return html.toString();
    }
}
