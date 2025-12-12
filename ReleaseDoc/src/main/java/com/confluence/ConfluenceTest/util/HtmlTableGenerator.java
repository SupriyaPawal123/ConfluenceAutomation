package com.confluence.ConfluenceTest.util;

import java.util.List;
import java.util.Map;

public class HtmlTableGenerator {

    /**
     * Generate an HTML table from a list of row maps.
     * The header order is taken from the first row's keys.
     */
    public static String generateTableHtml(List<Map<String, String>> rows) {
        StringBuilder sb = new StringBuilder();

        if (rows == null || rows.isEmpty()) {
            // Return an empty table with header placeholder
            return "<table><thead><tr><th>No data</th></tr></thead><tbody></tbody></table>";
        }

        // Header order from first row
        Map<String, String> firstRow = rows.get(0);
        sb.append("<table>");
        sb.append("<thead><tr>");
        for (String header : firstRow.keySet()) {
            sb.append("<th>").append(escapeHtml(header)).append("</th>");
        }
        sb.append("</tr></thead>");

        sb.append("<tbody>");
        for (Map<String, String> row : rows) {
            sb.append("<tr>");
            for (String header : firstRow.keySet()) {
                String value = row.getOrDefault(header, "");
                sb.append("<td>").append(escapeHtml(value)).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</tbody>");
        sb.append("</table>");

        return sb.toString();
    }

    // Very small HTML escaper to avoid accidental HTML injection
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
