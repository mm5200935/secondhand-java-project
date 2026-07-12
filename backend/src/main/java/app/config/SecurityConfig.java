package app.config;

import app.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // غیرفعال کردن CSRF چون از توکن JWT استفاده می‌کنیم
            .csrf(csrf -> csrf.disable())
            // تنظیمات CORS برای دسترسی فرانت‌اندهای مختلف
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // مدیریت دسترسی به مسیرها
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // مسیرهای ثبت‌نام و ورود برای همه آزاد است
                .requestMatchers("/api/admin/**").authenticated() // مسیرهای ادمین نیاز به لاگین دارند
                .anyRequest().authenticated() // بقیه مسیرها کاملاً نیازمند توکن هستند
            )
            // سیستم مدیریت سشن را روی Stateless می‌گذاریم (چون از توکن استفاده می‌شود نه سشن سرور)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // اضافه کردن فیلتر JWT قبل از فیلتر پیش‌فرض Spring Security
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // اجازه به تمامی مبداها (برای تست فرانت‌اند)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}