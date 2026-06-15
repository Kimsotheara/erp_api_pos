package com.theara.erp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    private static final String BEARER_SCHEME = "bearerAuth";
    private static final String OAUTH2_SCHEME = "oauth2";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ERP POS Platform API")
                        .version("1.0.0")
                        .description("""
                                Modular ERP POS REST API. Authenticate using an OAuth2 / OIDC access token (JWT) and click 'Authorize'.
                                """)
                        .contact(new Contact().name("Theara").email("ounkaa789@gmail.com"))
                        .license(new License().name("Proprietary")))
                .servers(List.of(new Server().url("/").description("Default")))

                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste a raw OAuth2 access token (JWT)."))
                        .addSecuritySchemes(OAUTH2_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("OAuth2 Authorization Code + PKCE flow.")
                                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                                        .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                                                .authorizationUrl("https://your-auth-server/oauth2/authorize")
                                                .tokenUrl("https://your-auth-server/oauth2/token")
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                        .addString("profile", "User profile")
                                                        .addString("erp.read", "Read access")
                                                        .addString("erp.write", "Write access"))))));
    }
}
