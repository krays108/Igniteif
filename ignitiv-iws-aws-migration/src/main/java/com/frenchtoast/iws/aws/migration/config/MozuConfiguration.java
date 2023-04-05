package com.frenchtoast.iws.aws.migration.config;

import com.frenchtoast.iws.aws.migration.service.MozuTokenService;
import com.mozu.api.MozuApiContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MozuConfiguration {

  @Value("${tenant.id}")
  private String tenantId;

  @Value("${site.id}")
  private String siteId;

  @Autowired
  private MozuTokenService mozuTokenService;

  @Bean
  public MozuApiContext mozuApiContext() {
    MozuApiContext apiContext = new MozuApiContext(Integer.parseInt(tenantId), Integer.parseInt(siteId));
    apiContext.setAppAuthTicket(mozuTokenService.getToken());
    return apiContext;
  }
}
