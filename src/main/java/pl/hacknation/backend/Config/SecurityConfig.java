package pl.hacknation.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Wyłączamy ochronę CSRF (niepotrzebna przy prostym REST API bez sesji)
                .csrf(AbstractHttpConfigurer::disable)
                // Włączamy obsługę CORS w Spring Security
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Konfiguracja uprawnień - ZEZWÓL NA WSZYSTKO
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()      // API dostępne dla każdego
                        .requestMatchers("/uploads/**").permitAll()  // Zdjęcia dostępne dla każdego
                        .requestMatchers("/error").permitAll()       // Błędy dostępne dla każdego
                        .anyRequest().permitAll()                    // Reszta też (dla pewności w dev)
                );

        return http.build();
    }

    // Globalna konfiguracja CORS (zastępuje tę z WebConfig i adnotacji)
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173")); // Twój frontend
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}