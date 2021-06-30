package com.freetonleague.core.util;

import com.freetonleague.core.domain.dto.UserImportExternalInfo;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility to deal with csv files
 */
public class CsvFileUtil {

    /**
     * Read scv file with user info to import data
     */
    public static List<UserImportExternalInfo> readCsvUserImportInfo(boolean hasHeader, InputStream importFile) {
        List<UserImportExternalInfo> records = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(importFile))) {
            CsvToBean<UserImportExternalInfo> csvToBean = new CsvToBeanBuilder(csvReader)
                    .withType(UserImportExternalInfo.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            Iterator<UserImportExternalInfo> csvUserIterator = csvToBean.iterator();
            //skip header
            if (hasHeader) {
                csvUserIterator.next();
            }
            while (csvUserIterator.hasNext()) {
                records.add(csvUserIterator.next());
            }
        } catch (Exception e) {
            throw new CustomUnexpectedException("Error while save export file to disk" + e.getMessage());
        }
        return records;
    }

    /**
     * Write scv file to disk and outputStream with user info
     */
    public static void writeCsvUserImportInfo(List<UserImportExternalInfo> stringArray, String path) throws CustomUnexpectedException {
        try (Writer writer = Files.newBufferedWriter(Paths.get(path));
             CSVWriter csvWriter = new CSVWriter(writer,
                     CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {

            StatefulBeanToCsv<UserImportExternalInfo> beanToCsv = new StatefulBeanToCsvBuilder(csvWriter)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();

            String[] entries = {"service", "user_id", "user_address", "leagueId"};
            csvWriter.writeNext(entries);
            beanToCsv.write(stringArray);
        } catch (Exception e) {
            throw new CustomUnexpectedException("Error while save export file to disk" + e.getMessage());
        }
    }

    public static void saveFileToOutputStream(String path, OutputStream outputStream) throws CustomUnexpectedException {
        try {
            Files.copy(Paths.get(path), outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new CustomUnexpectedException("Error while read exported csv and send to outputStream" + e.getMessage());
        }
    }
}
