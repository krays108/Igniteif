package com.frenchtoast.iws.aws.migration.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.frenchtoast.iws.aws.migration.exception.IntegrationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ML2NGHelper {
    private static final String NG_CUSTOM_SCHEMA_PATH = "custom.ng_custom_Schema_export_path";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    private static final char SEPARATOR_COMMA = ',';
    private static final String DOUBLE_QUOTES = "\"";

    /**
     * @return It returns current week of current month
     */
    public Integer getWeekOfMonth() {
        return Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * @return It returns date and time in desired format
     */
    public String getTimeStamp() {
        return SDF.format(new Timestamp(System.currentTimeMillis()));
    }


    /**
     * @param folder
     * @param fileName
     * @throws IntegrationException delete file from desired directory
     */
    public void deleteFile(String folder, String fileName) throws IntegrationException {
        try {
            File dir = new File(folder);
            File[] files = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(fileName);
                }
            });
            if (files != null && files.length > 0) {
                for (File file : files) {
                    try {
                        file.delete();
                    } catch (Exception e) {
                        log.error("Exception in deleting file::: ", e);
                    }
                }
            }
        } catch (Exception e) {
            throwError("Exception while deleting file ", e);
        }
    }
    public String getStringValueFromJson(JsonNode jsonNode, String fieldName) {
        String response = "";
        if (jsonNode != null && jsonNode.has(fieldName) && jsonNode.get(fieldName) != null) {
            response = jsonNode.get(fieldName).asText();
        }

        return response;
    }
    public String getJsonInnerList(Iterator<JsonNode> elements) {
        List<String> list = new ArrayList<>();
        while (elements.hasNext()) {
            list.add(elements.next().asText());
        }
        return DOUBLE_QUOTES + StringUtils.join(list,SEPARATOR_COMMA) + DOUBLE_QUOTES;
    }
    /**
     * This method log and then throw the error with the same log message.
     *
     * @param message - Log message
     * @param e       - Exception
     */
    public void throwError(String message, Throwable e) throws IntegrationException {
        log.error(message, e);
        if (e instanceof IntegrationException) {
            throw (IntegrationException) e;
        } else if (e != null) {
            throw new IntegrationException(message, e);
        } else {
            throw new IntegrationException(message);
        }
    }



}




