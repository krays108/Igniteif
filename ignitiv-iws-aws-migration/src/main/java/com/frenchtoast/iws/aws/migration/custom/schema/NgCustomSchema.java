package com.frenchtoast.iws.aws.migration.custom.schema;

import com.frenchtoast.iws.aws.migration.util.ML2NGHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("ngCustomSchema")
public class NgCustomSchema implements Processor {

  @Autowired
  private NgEmbroideriesSchemaExport ngEmbroideriesSchemaExport;

  @Autowired
  protected ML2NGHelper ml2NGHelper;

  @Override
  public void process(Exchange exchange) {
    log.info("NGCustomSchema::process({}) started...", exchange);
    List<String> errorCustomSchemaExport = new ArrayList<>();
    List<INgCustomSchemaExport> customSchemaList = Arrays.asList(ngEmbroideriesSchemaExport);

    customSchemaList.forEach(customSchema -> {
      log.info("Processing {}", customSchema.getName());
      try {
        customSchema.extractDataToFile();
        TimeUnit.SECONDS.sleep(2);
      } catch (Exception e) {
        errorCustomSchemaExport.add(customSchema.getName());
      }
    });

    if (!errorCustomSchemaExport.isEmpty()) {
      ml2NGHelper.throwError(String.format("Error while exporting the custom schema %s",errorCustomSchemaExport), null);
    }

    log.debug("NGCustomSchema::process({}) completed.", exchange);
  }
}