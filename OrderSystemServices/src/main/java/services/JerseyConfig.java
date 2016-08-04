package services;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * Created by gregorylaflash on 7/31/16.
 */

@Component
public class JerseyConfig extends ResourceConfig {

    @Value("${spring.jersey.application-path:/}")
    private String apiPath;

    public JerseyConfig() {
        // Add additional features such as support for Multipart.
        register(MultiPartFeature.class);
        register(CORSResponseFilter.class);
        register(UserService.class);
        register(PurchaseOrderService.class);
    }

    @PostConstruct
    public void init() {
        // Register components where DI is needed
        this.configureSwagger();
    }

    private void configureSwagger() {
        // Available at localhost:port/swagger.json
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        config.setConfigId("intech-southwest-order-management-system");
        config.setTitle("Intech Southwest Order Management System");
        config.setVersion("v1");
        config.setContact("Gregory LaFlash");
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath(this.apiPath);
        config.setResourcePackage("services");
        config.setPrettyPrint(true);
        config.setScan(true);
    }

}
