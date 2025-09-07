package com.example.pizzashop.configurations;

import com.example.pizzashop.filters.JwtTokenFilter;
import com.example.pizzashop.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;
import static org.springframework.http.HttpMethod.*;
@Configuration
//@EnableMethodSecurity
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final JwtTokenFilter jwtTokenFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws  Exception{
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests->{
                    requests.requestMatchers(
                                    String.format("%s/users/register",apiPrefix),
                                    String.format("%s/users/login",apiPrefix),
                                    String.format("%s/users/forgot-password",apiPrefix)
                            )
                            .permitAll()


                            .requestMatchers(GET,
                                    String.format("%s/pizzas**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/pizzas/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/pizzas/images/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/pizzas/images**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/pizzas**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/pizzas/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/pizzas/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/pizzas/**",apiPrefix)).hasAnyRole(Role.ADMIN)

                            .requestMatchers(POST,
                                    String.format("%s/orders/**",apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/orders/**",apiPrefix)).permitAll()
                            .requestMatchers(PUT,
                                    String.format("%s/orders/**",apiPrefix)).permitAll()
                            .requestMatchers(DELETE,
                                    String.format("%s/orders/**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/order_details/**",apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(GET,
                                    String.format("%s/order_details/**",apiPrefix)).permitAll()
                            .requestMatchers(PUT,
                                    String.format("%s/order_details/**",apiPrefix)).permitAll()
                            .requestMatchers(DELETE,
                                    String.format("%s/order_details/**",apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/healthcheck/**",apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/sizes**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/sizes/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/sizes**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/sizes/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/sizes/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/sizes/**",apiPrefix)).hasAnyRole(Role.ADMIN)

                            .requestMatchers(GET,
                                    String.format("%s/types**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/types/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/types**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/types/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/types/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/types/**",apiPrefix)).hasAnyRole(Role.ADMIN)

                            .requestMatchers(GET,
                                    String.format("%s/membership**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/membership/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/membership/phone/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/membership**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/membership/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/membership/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/membership**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/membership**",apiPrefix)).hasAnyRole(Role.ADMIN)

                            .requestMatchers(POST,
                                    String.format("%s/payments**",apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(POST,
                                    String.format("%s/payments/**",apiPrefix)).hasAnyRole(Role.USER)

                            .requestMatchers(GET,
                                    String.format("%s/tablebookings**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/tablebookings/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/tablebookings**",apiPrefix)).hasAnyRole(Role.USER, Role.STAFF)
                            .requestMatchers(POST,
                                    String.format("%s/tablebookings/**",apiPrefix)).hasAnyRole(Role.USER, Role.STAFF)
                            .requestMatchers(PUT,
                                    String.format("%s/tablebookings/**",apiPrefix)).hasAnyRole(Role.USER, Role.STAFF)
                            .requestMatchers(DELETE,
                                    String.format("%s/tablebookings/**",apiPrefix)).hasAnyRole(Role.USER, Role.STAFF)

                            .requestMatchers(GET,
                                    String.format("%s/review**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/review/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/review/pizza/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/review/pizza**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/review/order/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/review/order**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/review**",apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(POST,
                                    String.format("%s/review/**",apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(PUT,
                                    String.format("%s/review/**",apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(PUT,
                                    String.format("%s/review**",apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(DELETE,
                                    String.format("%s/review/**",apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/users**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/users/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/users/details/**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/users/details**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/users**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/users/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/users/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/users**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/users/**",apiPrefix)).hasAnyRole(Role.ADMIN)


                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable);

        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATH","DELETE","OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization","content-type","x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**",configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }
}
//eyJhbGciOiJIUzI1NiJ9.eyJwaG9uZU51bWJlciI6IjA5OTk5OTk5OTgiLCJ1c2VySWQiOjExLCJzdWIiOiIwOTk5OTk5OTk4IiwiZXhwIjoxNzQ4Nzg3Njc5fQ.UZNMIHJ5BNW_GY_L4fugbKM-NEI7mJ3EeegjkuPQIoE