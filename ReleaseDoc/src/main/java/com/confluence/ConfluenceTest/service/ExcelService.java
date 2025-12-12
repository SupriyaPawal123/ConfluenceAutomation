package com.confluence.ConfluenceTest.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ExcelService {

    public Map<String, List<Map<String,String>>> formatExcelData(MultipartFile file) throws IOException {

        Map<String, List<Map<String,String>>> returnMap = new LinkedHashMap<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            for (Sheet sheet : workbook) {

                List<Map<String,String>> mapList = new ArrayList<>();
                List<String> headers = new ArrayList<>();

                int rowIndex = 0;

                for (Row row : sheet) {
                    if (rowIndex == 0) {
                        // header row
                        for (Cell cell : row) {
                            headers.add(getCellValueAsString(cell));
                        }
                    } else {
                        Map<String, String> data = new LinkedHashMap<>();
                        int colIdx = 0;

                        for (Cell cell : row) {
                            if (colIdx < headers.size()) {
                                data.put(headers.get(colIdx), getCellValueAsString(cell));
                            }
                            colIdx++;
                        }
                        mapList.add(data);
                    }
                    rowIndex++;
                }

                returnMap.put(sheet.getSheetName(), mapList);
            }
        }

        return returnMap;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
