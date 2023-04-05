package com.frenchtoast.iws.aws.migration.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

public class HelloWorldRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("quartz://hello-world-java?cron={{hello-world.cron}}")
                .transform().constant("Hello world from java class")
                .to("log:hello-world-java");
    }
}
