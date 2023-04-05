package com.frenchtoast.iws.aws.migration.service;

import com.mozu.api.MozuConfig;
import com.mozu.api.contracts.appdev.AppAuthInfo;
import com.mozu.api.security.AppAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MozuTokenService {

  @Value("${application.key}")
  private String kiboApplicationKey;

  @Value("${shared.secret}")
  private String kiboApplicationSecret;

  @Value("${base.app.auth.url}")
  private String baseAppAuthUrl;

  @Value("${default.event.request.timeout}")
  private String defaultEventRequestTimeout;

  public String getToken() {

    String METHOD_NAME = "getToken";
    log.debug("Entering method={}", METHOD_NAME);
    AppAuthInfo appAuthInfo = createAppAuthInfo();
    AppAuthenticator.initialize(appAuthInfo);
    AppAuthenticator appAuthenticator = AppAuthenticator.getInstance();
    String appAuthTicket = appAuthenticator.getAppAuthTicket().getAccessToken();
    log.info("Application authenticated");
    log.debug("Leaving method={}", METHOD_NAME);
    return appAuthTicket;
  }

  private AppAuthInfo createAppAuthInfo() {
    AppAuthInfo appAuthInfo = new AppAuthInfo();
    appAuthInfo.setApplicationId(kiboApplicationKey);
    appAuthInfo.setSharedSecret(kiboApplicationSecret);

    if (baseAppAuthUrl != null && !baseAppAuthUrl.trim().equals(""))
      MozuConfig.setBaseUrl(baseAppAuthUrl);

    try {
      if (defaultEventRequestTimeout != null && !defaultEventRequestTimeout.trim().equals(""))
        MozuConfig.setDefaultEventRequestTimeout(Integer.parseInt(defaultEventRequestTimeout));
    } catch (Exception e) {
      log.error("Failed to initialize timeout. Setting default to 10000", e);
      MozuConfig.setDefaultEventRequestTimeout(10000);
    }
    return appAuthInfo;
  }
}
