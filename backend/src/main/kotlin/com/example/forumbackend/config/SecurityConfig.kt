package com.example.forumbackend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    /**
     * Filter chain: every path is permitted, nothing else is enforced.
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }

            //  ─── THIS is the single line that opens everything ───
            .authorizeHttpRequests { auth -> auth.anyRequest().permitAll() }
            //  ─────────────────────────────────────────────────────

            // No JWT parser, no custom filters, no session
            .build()

    /* ---------------- helper beans still used by your services ---------------- */

    @Bean
    fun authenticationManager(cfg: AuthenticationConfiguration): AuthenticationManager =
        cfg.authenticationManager       // needed by AuthService.login()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource =
        UrlBasedCorsConfigurationSource().apply {
            val cfg = CorsConfiguration().apply {
                allowedOrigins  = listOf("*")
                allowedMethods  = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                allowedHeaders  = listOf("Authorization", "Content-Type", "X-Requested-With")
                exposedHeaders  = listOf("Authorization")
                allowCredentials = false
                maxAge = 3600
            }
            registerCorsConfiguration("/**", cfg)
        }
}