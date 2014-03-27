package org.test.service;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class CheckoutService extends Service<CheckoutConfiguration> {
    public static void main(String[] args) throws Exception {
        new CheckoutService().run(args);
    }

    @Override
    public void initialize(Bootstrap<CheckoutConfiguration> bootstrap) {
        bootstrap.setName("checkout-service");
    }

    @Override
    public void run(CheckoutConfiguration configuration,
                    Environment environment) {
        
        environment.manage(new MessageProcessing());
        environment.addResource(new CheckoutResource());
        
        
    }

}