package com.frenchtoast.iws.aws.migration.custom.schema;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class NgEmbroideriesSchemaExport extends INgCustomSchemaExport {

  private static final String EMBROIDERIES_SCHEMA = "embroideries@ft";
  private static final String COLUMN_SOURCE_CODE = "sourceCode";
  private static final String COLUMN_LOGO = "logo";
  private static final String COLUMN_NAME = "name";
  private static final String COLUMN_PRICE = "price";
  private static final String COLUMN_SCHOOL_ID = "schoolId";
  private static final String COLUMN_CHECKED = "checked";
  private static final String COLUMN_IS_SELECTED = "isSelected";
  private static final String COLUMN_DISCOUNTED_PRICE = "discountedPrice";
  private static final String COLUMN_MESSAGE = "message";

  public NgEmbroideriesSchemaExport() {
    super(EMBROIDERIES_SCHEMA, getHeaders());
  }

  public static Set<String> getHeaders() {
    return new LinkedHashSet<>(Arrays.asList(COLUMN_SOURCE_CODE, COLUMN_LOGO, COLUMN_NAME, COLUMN_PRICE,
        COLUMN_SCHOOL_ID, COLUMN_CHECKED, COLUMN_IS_SELECTED, COLUMN_DISCOUNTED_PRICE, COLUMN_MESSAGE));
  }

  public String getCsvRow(JsonNode jsonNode) {
    List<String> items = new LinkedList<>(Arrays.asList(
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_SOURCE_CODE),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_LOGO),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_NAME),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_PRICE),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_SCHOOL_ID),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_CHECKED),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_IS_SELECTED),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_DISCOUNTED_PRICE),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_MESSAGE)));

    return StringUtils.join(items.toArray(), ",");
  }
}

