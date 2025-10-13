package com.eplatform.b2b.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8120");
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setEmail("dev@eplatform.com");
        contact.setName("E-Platform B2B Team");

        License license = new License();
        license.setName("MIT License");
        license.setUrl("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Product Service API")
                .version("1.0")
                .description("REST API for Product Management in B2B E-commerce Platform")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
