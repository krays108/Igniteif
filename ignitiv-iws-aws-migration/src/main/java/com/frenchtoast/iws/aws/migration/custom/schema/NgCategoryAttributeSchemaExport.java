package com.frenchtoast.iws.aws.migration.custom.schema;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class NgCategoryAttributeSchemaExport extends INgCustomSchemaExport {

  private static final String CATEGORY_ATTRIBUTE_SCHEMA = "categoryattributes@ft";

  private static final String COLUMN_ID = "id";
  private static final String COLUMN_STATUS = "STATUS";
  private static final String COLUMN_TOP_NAV_HEADER = "topNavHeader";
  private static final String COLUMN_MAIN_IMAGE = "mainImage";
  private static final String COLUMN_MESSAGE = "message";
  private static final String COLUMN_MAIN_URL = "mainUrl";
  private static final String COLUMN_IS_DRESS_CODE = "isDressCode";

  public NgCategoryAttributeSchemaExport() {
    super(CATEGORY_ATTRIBUTE_SCHEMA, getHeaders());
  }

  /**
   * This method get the header names of CSV file to be generated.
   *
   * @return - Header names as Set.
   */
  public static Set<String> getHeaders() {
    return new LinkedHashSet<>(Arrays.asList(COLUMN_ID, COLUMN_STATUS, COLUMN_TOP_NAV_HEADER,
        COLUMN_MAIN_IMAGE, COLUMN_MESSAGE, COLUMN_MAIN_URL, COLUMN_IS_DRESS_CODE));
  }

  @Override
  public String getCsvRow(JsonNode jsonNode) {
    List<String> items = new LinkedList<>(Arrays.asList(
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_ID),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_STATUS),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_TOP_NAV_HEADER),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_MAIN_IMAGE),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_MESSAGE),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_MAIN_URL),
        ml2NGHelper.getStringValueFromJson(jsonNode, COLUMN_IS_DRESS_CODE)));

    return StringUtils.join(items.toArray(), ",");
  }
}
