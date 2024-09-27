package com.bytmasoft.dss.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(

        info = @Info(
                contact = @Contact(
                        name = "Mahamat Abakar",
                        email = "abakar61@web.de",
                        url = ""
                ),
                description = "OpenApi for Document Management Service",
                title = "DMS",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "http://localhost"
                ),
                termsOfService = "Term of Service"

        ),
        tags = {
                @Tag(name = "Documents", description = "Document Management Service for all documents")
        }

        //,
        //,
        /* servers = {

                 @Server(
                         description = "Local ENV",
                         url = "http://localhost:8081"
                 ),
                 @Server(
                         description = "Prod ENV",
                         url = "http://www.prod:8081"
                 )
         },*/
//        security = {
//                @SecurityRequirement(
//                        name = "BearerAuth"
//                )
//        }
)
//@SecuritySchemes(
//        value = {
//                @SecurityScheme(
//                        name = "BearerAuth",
//                        description = "Jwt authentication",
//                        scheme = "Bearer",
//                        type = SecuritySchemeType.HTTP,
//                        bearerFormat = "JWT",
//                        in = SecuritySchemeIn.HEADER
//                )
//        }
//)

@Configuration
public class OpenApiConfig {
}
