package mipkasnow.dev.rococo.config;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Component
public class CorsCustomizer {

    @Value("${rococo-front.base-uri}")
    private String frontUri;

    public void apply(@Nonnull HttpSecurity http) throws Exception {
        http.cors(customizer());
    }

    private Customizer<CorsConfigurer<HttpSecurity>> customizer() {
        return configurer -> configurer.configurationSource(corsConfigurationSource());
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowCredentials(true);
            configuration.setAllowedOrigins(List.of(frontUri));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setAllowedMethods(List.of("*"));
            return configuration;
        };
    }
}
