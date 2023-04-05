package com.frenchtoast.iws.aws.migration.model;

/**
 * Context class to hold tenant and site id
 * @author Ignitiv
 */
public final class Context {

    private Integer tenantId;
    private Integer siteId;

    /**
     * @param tenantId
     *            - Used in absence of Site Id not available.
     */
    public Context(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Context(Integer tenantId, Integer siteId) {
        this.tenantId = tenantId;
        this.siteId = siteId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setTenantId(final Integer tenantId) {
        this.tenantId = tenantId;
    }

    public void setSiteId(final Integer siteId) {
        this.siteId = siteId;
    }
}
