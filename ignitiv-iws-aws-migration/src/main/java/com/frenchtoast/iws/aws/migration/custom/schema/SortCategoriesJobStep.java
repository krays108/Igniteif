package com.frenchtoast.iws.aws.migration.custom.schema;

import java.util.Hashtable;
import java.util.Iterator;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.productadmin.Category;
import com.mozu.api.contracts.productadmin.CategoryPagedCollection;
import com.mozu.api.resources.commerce.catalog.admin.CategoryResource;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by rajni.ubhi on 14/05/2018.
 */
//@PlatformComponent("sortCategoriesJobStep")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
@Component("sortCategoriesJobStep")
public class SortCategoriesJobStep implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(SortCategoriesJobStep.class);

    private static final String SCHOOLS_FILTER = "categoryCode eq SCHOOLS";
    private static final String FILTER_PARENT_CATEGORY_ID_PREFIX = "parentCategoryId eq ";
    private static final String  NOTIFICATION_KEY_STATUS = "NOTIFICATION_KEY_STATUS";
    private static final String NOTIFICATION_MSG_TYPE_SUCCESS ="SUCCESS";
    private static final String NOTIFICATION_KEY_MESSAGE = "NOTIFICATION_KEY_MESSAGE";
    private static final String NOTIFICATION_MSG_TYPE_ERROR = "NOTIFICATION_MSG_TYPE_ERROR";
    private static final int PAGE_SIZE = 200;
    private static final int PAGE_SIZE_10 = 10;
    private static final String SORT_BY_NAME = "name";

    @Autowired
    private MozuApiContext mozuApiContext;

    private static CategoryResource categoryResource;

    private CategoryResource getCategoryResource() {
        if (categoryResource == null) {
            categoryResource = new CategoryResource(mozuApiContext);
        }
        return categoryResource;
    }
    

	@Override
	public void process(Exchange exchange) {
		log.info("SortCategoriesJobStep::process({}) started...", exchange);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		optimizedSortCategory(exchange);
		stopWatch.stop();
		log.debug("Time taken to sort in MilliSeconds" + stopWatch.getTime());
		stopWatch.getTime();
	}

   // @Override
    public Object executeStep(Object inputParm) {
        String message;
        Hashtable<String, String> returnCode = new Hashtable<>();

        try {
            int schoolsId = getSchoolsId();
            log.info("schoolsId::process({}) started..."+schoolsId); 
            int maxSequence = getMaxSequence(schoolsId);
            moveCategories(schoolsId, maxSequence);
//            moveCategories(schoolsId, 0);

            message = "SortCategories Job has been run Successfully.";
            returnCode.put(NOTIFICATION_KEY_STATUS, NOTIFICATION_MSG_TYPE_SUCCESS);
            returnCode.put(NOTIFICATION_KEY_MESSAGE, message);
        } catch (Exception e) {
            logger.error("Exception in executeStep ", e);
            message = "Exception occurred in SortCategories Job. " + e.getMessage();
            returnCode.put(NOTIFICATION_KEY_STATUS, NOTIFICATION_MSG_TYPE_ERROR);
            returnCode.put(NOTIFICATION_KEY_MESSAGE, message);
        }

        return returnCode;
    }

    /**
     * This method gets the id of SCHOOLS category.
     *
     * @return - SCHOOLS id
     * @throws Exception - Exception in retrieving SCHOOLS id.
     */
    private int getSchoolsId() throws Exception {
        try {
            int id = -1;
            CategoryPagedCollection collection = getCategoryResource().getCategories(0, PAGE_SIZE, null, SCHOOLS_FILTER, null);
            for (Category category : collection.getItems()) {
                if (category != null) {
                    id = category.getId();
                    break;
                }
            }

            if (id < 0) {
                throw new Exception("Exception in getting SCHOOLS category ID ");
            }

            return id;
        } catch (Exception e) {
            logger.error("Exception in getting SCHOOLS category ID ", e);
            throw e;
        }
    }

    /**
     * This method retrieves maximum sequence assigned to school categories.
     *
     * @param schoolId - SCHOOLS category id.
     * @return - Maximum sequence found.
     * @throws Exception - Exception in retrieves maximum sequence assigned to school categories.
     */
    private int getMaxSequence(int schoolId) throws Exception {
        try {
        	 StopWatch stopWatch = new StopWatch();
        	 stopWatch.start();
        	log.info("getMaxSequence ::: Started");
            int maxSequence = -1;
            int startIndex = 0;
            String filter = FILTER_PARENT_CATEGORY_ID_PREFIX + schoolId;
            log.info("filter :::"+filter);
            CategoryPagedCollection collection = getCategoryResource().getCategories(startIndex, PAGE_SIZE, null, filter, null);
            Iterator<Category> iterator = collection.getItems().iterator();
            while (iterator.hasNext()) {
                Category category = iterator.next();
                maxSequence = Math.max(category.getSequence(), maxSequence);
                if (!iterator.hasNext() && collection.getItems().size() > 0) {
                    startIndex += PAGE_SIZE;
                    collection = getCategoryResource().getCategories(startIndex, PAGE_SIZE, null, filter, null);
                    iterator = collection.getItems().iterator();
                }
            }
            maxSequence = Math.max(collection.getTotalCount(), maxSequence);

            if (maxSequence < 0) {
                throw new Exception("Exception in finding max sequence ");
            }
            stopWatch.stop();
            log.info("Total Time Taken :::::"+stopWatch.getTime());
            return maxSequence;
        } catch (Exception e) {
            logger.error("Exception in finding max sequence ", e);
            throw e;
        }
    }

    /**
     * This method moves categories starting with sequence provided.
     *
     * @param schoolId    - SCHOOLS category id.
     * @param maxSequence - Sequence to start from.
     * @throws Exception - Exception in moving categories.
     */
    private void moveCategories(int schoolId, int maxSequence) throws Exception {
        try {
        	
        	int count = 0;
        	log.info("moveCategories::process({}) started..."+maxSequence); 
            int startIndex = 0;
            String filter = FILTER_PARENT_CATEGORY_ID_PREFIX + schoolId;
            CategoryPagedCollection collection = getCategoryResource().getCategories(startIndex, PAGE_SIZE, SORT_BY_NAME, filter, null);
            Iterator<Category> iterator = collection.getItems().iterator();
            log.info("collection::process({}) started..."+collection.getItems().size()); 
            while (iterator.hasNext()) {
                Category category = iterator.next();
                log.info("Category::process({}) started..."+category.getContent().getName() +"Count value  "+ count++); 
                moveCategory(category, ++maxSequence);
                if (!iterator.hasNext() && collection.getItems().size() > 0) {
                    startIndex += PAGE_SIZE;
                    collection = getCategoryResource().getCategories(startIndex, PAGE_SIZE, SORT_BY_NAME, filter, null);
                    iterator = collection.getItems().iterator();
                }
            }
        } catch (Exception e) {
            logger.error("Exception in moving school categori"
            		+ "es ", e);
            throw e;
        }
    }
    
    
	private Object optimizedSortCategory(Object inputParm) {

		String message;
		Hashtable<String, String> returnCode = new Hashtable<>();
		try {
			int schoolsId = getSchoolsId();
			int startIndex = 0;
			int count = 0;
			Integer sequence = 0;
			String responseFields = "items(id,sequence,content)";
			String filter = FILTER_PARENT_CATEGORY_ID_PREFIX + schoolsId;
			CategoryPagedCollection collection = getCategoryResource().getCategories(startIndex, PAGE_SIZE,
					SORT_BY_NAME, filter, responseFields);
			Iterator<Category> iterator = collection.getItems().iterator();
			while (iterator.hasNext()) {
				Category category = iterator.next();
				count++;
				sequence++;
				log.debug("Category::process({}) started..." + category.getContent().getName() + "Sequence ::::"
						+ category.getSequence() + "Count value  " + count);
				if (!category.getSequence().toString().equals(sequence.toString())) {
					moveCategory(category.getId(), sequence);
				}
				if (!iterator.hasNext() && count < collection.getTotalCount()) {
					startIndex += PAGE_SIZE;
					collection = getCategoryResource().getCategories(startIndex, PAGE_SIZE, SORT_BY_NAME, filter,
							responseFields);
					iterator = collection.getItems().iterator();
				}
			}

			message = "SortCategories Job has been run Successfully.";
			returnCode.put(NOTIFICATION_KEY_STATUS, NOTIFICATION_MSG_TYPE_SUCCESS);
			returnCode.put(NOTIFICATION_KEY_MESSAGE, message);
		} catch (Exception e) {
			logger.error("Exception in executeStep ", e);
			message = "Exception occurred in SortCategories Job. " + e.getMessage();
			returnCode.put(NOTIFICATION_KEY_STATUS, NOTIFICATION_MSG_TYPE_ERROR);
			returnCode.put(NOTIFICATION_KEY_MESSAGE, message);
		}

		return returnCode;

	}
    

    /**
     * This method move given category to a new sequence provided.
     *
     * @param category    - Category
     * @param newSequence - New sequence.
     */
    private void moveCategory(Category category, int newSequence) {
        try {
            category.setSequence(newSequence);
            getCategoryResource().updateCategory(category, category.getId());
        } catch (Exception e) {
            logger.error("Exception in moving category " + category.getCategoryCode(), e);
        }
    }
    
    private void moveCategory(int categoryId, int newSequence) {
    	Category category = null;
    	try {
            category = getCategoryResource().getCategory(categoryId);
            int categorySequence = category.getSequence();
            log.info("Category is being moved from position "+category.getSequence()+ "to position :"+newSequence);
            category.setSequence(newSequence);
            if(categorySequence != newSequence)
            getCategoryResource().updateCategory(category, category.getId());
        } catch (Exception e) {
            logger.error("Exception in moving category " + category.getCategoryCode(), e);
        }
    }
}
