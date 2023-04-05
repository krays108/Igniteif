package com.frenchtoast.iws.aws.migration.custom.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.frenchtoast.iws.aws.migration.exception.IntegrationException;
import com.frenchtoast.iws.aws.migration.util.ML2NGHelper;
import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.mzdb.EntityCollection;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class INgCustomSchemaExport {

    private static final String FILE_TYPE_CSV = ".csv";
    private static final String WEEK = "Week";
    private static final String UNDERSCORE = "_";
    private static final char SEPARATOR = ',';
    @Autowired
    protected ML2NGHelper ml2NGHelper;


    @Autowired
    private MozuApiContext mozuApiContext;
    private String name;
    private Set<String> headers;
    private EntityResource entityResource;
    INgCustomSchemaExport(String name, Set<String> headers) {
        this.name = name;
        this.headers = headers;
    }
    @PostConstruct
    public void init() {
        this.entityResource = new EntityResource(mozuApiContext);
    }
    /**
     * This methods gets the CSV file row for a given json instance.
     *
     * @param jsonNode - JSON row
     * @return - CSV file row.
     */
    protected abstract String getCsvRow(JsonNode jsonNode);

    /**
     * Getter method for name.
     */
    public String getName() {
        return name;
    }

    /**
     * This method extracts custom schema data to a CSV file.
     */
    public void extractDataToFile() {
        String folder = "D:/Projects/Code/test_ftp_path/";
        String fileName = "ng-custom-schema-embroideries";
        String fileNamePostfix =  FILE_TYPE_CSV;
        ml2NGHelper.deleteFile(folder, fileName);
        try (PrintWriter file = new PrintWriter(folder + fileName + fileNamePostfix)) {
            String headerRow = StringUtils.join(headers, SEPARATOR);
            log.debug("Writing the header row.");
            file.println(headerRow);

            int startIndex = 0;
            int pageSize = 200;
            EntityCollection entityCollection = entityResource.getEntities(name, pageSize, startIndex, null, null,
                null);
            Iterator<JsonNode> iterator = entityCollection.getItems().iterator();
            while (iterator.hasNext()) {
                JsonNode jsonNode = iterator.next();
                try {
                    String row = getCsvRow(jsonNode);
                    file.write(row + "\n");
                } catch (Exception e) {
                    log.error("Exception in writing CSV row.", e);
                }

                if (!iterator.hasNext() && entityCollection.getItems().size() > 0) {
                    startIndex += pageSize;
                    entityCollection = entityResource.getEntities(name, pageSize, startIndex, null, null, null);
                    iterator = entityCollection.getItems().iterator();
                }
            }

            file.flush();
            file.close();
        } catch (IntegrationException e) {
            ml2NGHelper.throwError(e.getMessage(), e);
        } catch (Exception e) {
            ml2NGHelper.throwError("Exception while fetching schema collection ", e);
        }
    }
}
