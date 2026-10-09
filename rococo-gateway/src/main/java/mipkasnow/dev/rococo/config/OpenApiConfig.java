package mipkasnow.dev.rococo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String OPEN_API_AUTH_SCHEME = "bearer";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rococo Gateway")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(OPEN_API_AUTH_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(OPEN_API_AUTH_SCHEME, new SecurityScheme()
                                .name(OPEN_API_AUTH_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
