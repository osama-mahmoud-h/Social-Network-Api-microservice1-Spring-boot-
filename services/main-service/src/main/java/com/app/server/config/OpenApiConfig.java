package com.app.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${osama_mh.openapi.dev-url:http://localhost:8083}")
    private String devUrl;

    @Value("${osama_mh.openapi.prod-url:http://localhost:8082}")
    private String prodUrl;

    @Value("${osama_mh.openapi.localhost-url:http://localhost:8083}")
    private String localhostUrl;

    @Bean
    public OpenAPI myOpenAPI() {

        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Server localhostServer = new Server();
        localhostServer.setUrl(localhostUrl);
        localhostServer.setDescription("Server URL in Localhost environment");


        Contact contact = new Contact();
        contact.setEmail("osama.mahmoud.h9@gmail.com");
        contact.setName("osama_mh");
        contact.setUrl("https://www.bezkoder.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");


        Info info = new Info()
                .title("Social Network API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for the Social Network microservices application.")
                .termsOfService("https://www.bezkoder.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localhostServer, devServer, prodServer))
                .addSecurityItem(new SecurityRequirement().addList("jwtAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("jwtAuth", securityScheme)
                );
    }
}