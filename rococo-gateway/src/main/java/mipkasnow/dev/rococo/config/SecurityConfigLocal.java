package mipkasnow.dev.rococo.config;

import lombok.RequiredArgsConstructor;
import mipkasnow.dev.rococo.config.context.UserContext;
import mipkasnow.dev.rococo.config.context.UserContextFilter;
import mipkasnow.dev.rococo.exception.ApiAccessDeniedHandler;
import mipkasnow.dev.rococo.exception.ApiAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@Profile({"local", "docker", "staging"})
@RequiredArgsConstructor
public class SecurityConfigLocal {

    private final CorsCustomizer corsCustomizer;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;
    private final ApiAccessDeniedHandler accessDeniedHandler;

    @Bean
    public UserContext userContext() {
        return new UserContext();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserContext userContext) throws Exception {
        corsCustomizer.apply(http);
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(customizer ->
                        customizer.requestMatchers(HttpMethod.GET, "/api/session").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/country", "/api/country/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/museum", "/api/museum/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/artist", "/api/artist/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/painting", "/api/painting/**").permitAll()
                                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationEntryPoint))
                .addFilterAfter(new UserContextFilter(userContext), BearerTokenAuthenticationFilter.class);
        return http.build();
    }
}
