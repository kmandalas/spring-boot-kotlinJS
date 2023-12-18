package dev.kmandalas.demo.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${defaultSuccessUrl}")
    private val defaultSuccessUrl: String? = null

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .cors(withDefaults())
            .csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                csrf.disable()
            }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/anonymous*")
                    .anonymous()
                    .requestMatchers(HttpMethod.GET, "/", "/error", "actuator/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .defaultAuthenticationEntryPointFor(
                        Http403ForbiddenEntryPoint(),
                        AntPathRequestMatcher("/api/**")
                    )
            }
            .formLogin { login ->
                login
                    .loginProcessingUrl("/perform_login")
                    .defaultSuccessUrl(defaultSuccessUrl, true)
                    .failureUrl("/index.html?error=true")
            }
            .logout { logout ->
                logout
                    .logoutUrl("/api/logout")
                    .deleteCookies("JSESSIONID")
            }

        return httpSecurity.build()
    }

    @Bean
    fun userDetailsService(): InMemoryUserDetailsManager {
        val user1: UserDetails = User.withUsername("user")
            .password("{noop}12345")
            .roles("USER")
            .build()

        val admin: UserDetails = User.withUsername("admin")
            .password("{noop}admin0Pass")
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(user1, admin)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("http://localhost:[*]")
        configuration.allowCredentials = true
        configuration.allowedMethods = listOf("GET", "POST", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf(
            "Content-Type",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Authorization",
            "X-Requested-With",
            "requestId",
            "Correlation-Id"
        )
        configuration.exposedHeaders = listOf("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}
