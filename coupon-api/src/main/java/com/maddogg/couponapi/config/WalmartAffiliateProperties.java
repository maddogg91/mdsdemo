package com.maddogg.couponapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Credentials/settings for pulling Walmart's deal feed from Impact.com, the affiliate
 * network Walmart's affiliate program runs on. Obtain accountSid/authToken and the
 * Walmart campaignId from your Impact.com media partner account after your Walmart
 * affiliate application is approved.
 */
@ConfigurationProperties(prefix = "walmart.affiliate")
public class WalmartAffiliateProperties {

    /** Master switch. False (the default) keeps WalmartCouponProvider on sample data. */
    private boolean enabled = false;

    private String baseUrl = "https://api.impact.com";

    private String accountSid;

    private String authToken;

    /** Walmart's campaign/program id within your Impact.com media partner account. */
    private String campaignId;

    private int pageSize = 50;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isConfigured() {
        return enabled
                && accountSid != null && !accountSid.isBlank()
                && authToken != null && !authToken.isBlank()
                && campaignId != null && !campaignId.isBlank();
    }
}
