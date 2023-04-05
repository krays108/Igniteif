package com.frenchtoast.iws.aws.migration.exception;

import java.util.HashMap;

public class IntegrationException extends RuntimeException {


    private static final long serialVersionUID = 1L;

    private String failedRecordName = "";

    private String failedRecordId = "";

    private int errorCode = -1;

    private HashMap<String, String> errorFields = new HashMap<>();

    public IntegrationException() {
    }

    public IntegrationException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }

    public IntegrationException(String pMessage) {
        super(pMessage);
    }

    public IntegrationException(Throwable pCause) {
        super(pCause);
    }

    public IntegrationException(int errorCode, String pMessage, Throwable pCause) {
        super(pMessage, pCause);
        this.errorCode = errorCode;
    }

    public IntegrationException(int errorCode, String pMessage) {
        super(pMessage);
        this.errorCode = errorCode;
    }

    public IntegrationException(int errorCode, Throwable pCause) {
        super(pCause);
        this.errorCode = errorCode;
    }

    public static IntegrationException newNullLocaleException() {
        IntegrationException ie = new IntegrationException(-10007, "Null locale provided in export request. Locale is required.");
        ie.getErrorFields().put("Locale", "");
        return ie;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getFailedRecordName() {
        return this.failedRecordName;
    }

    public void setFailedRecordName(String failedRecordName) {
        this.failedRecordName = failedRecordName;
    }

    public String getFailedRecordId() {
        return this.failedRecordId;
    }

    public void setFailedRecordId(String failedRecordId) {
        this.failedRecordId = failedRecordId;
    }

    public HashMap<String, String> getErrorFields() {
        return this.errorFields;
    }

    public void setErrorFields(HashMap<String, String> errorFields) {
        this.errorFields = errorFields;
    }
}


