package org.example.utility;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.entity.Person;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class SpreadsheetParser {

    private static final List<String> REQUIRED_HEADERS = List.of("first_name", "last_name", "email", "date_of_birth");

    public static List<Person> parse(MultipartFile file) throws IOException {
        FileType type = FileType.from(file.getOriginalFilename(), file.getContentType());
        return switch (type) {
            case CSV -> parseCsv(file.getInputStream());
            case XLS, XLSX -> parseExcel(file.getInputStream());
        };
    }

    private static List<Person> parseCsv(InputStream in) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines()
                     .withTrim()
                     .parse(reader)) {

            validateHeaders(parser.getHeaderMap().keySet());
            List<Person> persons = new ArrayList<>();
            for (CSVRecord record : parser) {
                persons.add(mapRow(toHeaderMap(record)));
            }
            return persons;
        }
    }

    private static List<Person> parseExcel(InputStream in) throws IOException {
        try (Workbook wb = createWorkbook(in)) {
            Sheet sheet = wb.getNumberOfSheets() > 0 ? wb.getSheetAt(0) : null;
            if (sheet == null) throw new IllegalArgumentException("No sheets found in workbook");

            Iterator<Row> rowIt = sheet.iterator();
            if (!rowIt.hasNext()) throw new IllegalArgumentException("Sheet is empty");
            Row headerRow = rowIt.next();
            List<String> headers = readHeaders(headerRow);
            validateHeaders(headers);

            List<Person> persons = new ArrayList<>();
            while (rowIt.hasNext()) {
                Row row = rowIt.next();
                Map<String, String> data = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    data.put(headers.get(i), readCellAsString(cell));
                }
                persons.add(mapRow(data));
            }
            return persons;
        }
    }

    private static Workbook createWorkbook(InputStream in) throws IOException {
        in = new BufferedInputStream(in);
        in.mark(8);
        try {
            return new XSSFWorkbook(in);
        } catch (Exception x) {
            in.reset();
            return new HSSFWorkbook(in);
        }
    }

    private static List<String> readHeaders(Row headerRow) {
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(readCellAsString(cell).toLowerCase());
        }
        return headers;
    }

    private static String readCellAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                    : stripTrailingZeros(cell.getNumericCellValue());
            case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private static String stripTrailingZeros(double d) {
        String s = Double.toString(d);
        if (s.endsWith(".0")) return s.substring(0, s.length() - 2);
        return s;
    }

    private static void validateHeaders(Collection<String> headers) {
        Set<String> set = new HashSet<>();
        for (String h : headers) set.add(h.toLowerCase());
        for (String required : REQUIRED_HEADERS) {
            if (!set.contains(required)) {
                throw new IllegalArgumentException("Missing required header: " + required + " (expected headers: " + REQUIRED_HEADERS + ")");
            }
        }
    }

    private static Map<String, String> toHeaderMap(CSVRecord record) {
        Map<String, String> map = new HashMap<>();
        for (String h : record.getParser().getHeaderNames()) {
            map.put(h.toLowerCase(), record.get(h));
        }
        return map;
    }

    private static Person mapRow(Map<String, String> data) {
        String firstName = data.getOrDefault("first_name", "").trim();
        String lastName = data.getOrDefault("last_name", "").trim();
        String email = data.getOrDefault("email", "").trim();
        String dobStr = data.getOrDefault("date_of_birth", "").trim();

        LocalDate dob = parseDate(dobStr);
        return new Person(firstName, lastName, email, dob);
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        List<DateTimeFormatter> fmts = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,           // 1990-05-20
                DateTimeFormatter.ofPattern("d/M/uuuu"),    // 20/5/1990
                DateTimeFormatter.ofPattern("M/d/uuuu")     // 5/20/1990
        );
        for (DateTimeFormatter f : fmts) {
            try { return LocalDate.parse(s, f); } catch (DateTimeParseException ignored) {}
        }
        // Last resort: Excel numeric date
        try {
            double excel = Double.parseDouble(s);
            return LocalDate.of(1899, 12, 30).plusDays((long) excel);
        } catch (NumberFormatException e) {
            return null; // Let validator handle if required
        }
    }
}