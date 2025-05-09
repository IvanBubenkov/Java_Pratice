package ru.msu.cmc.webprak.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.msu.cmc.webprak.DAO.SiteUserDAO;
import ru.msu.cmc.webprak.models.SiteUser;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SiteUserDAO siteUserDAO;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/auth/**", "/register/**").permitAll()
                        .requestMatchers("/adminPanel/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")// Только для админов
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/determine-role-redirect", true)  // Перенаправление через контроллер
                        .failureUrl("/auth?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth?logout=true")
                        .permitAll()
                )
                .userDetailsService(username -> {
                    SiteUser user = siteUserDAO.getUserByLogin(username);
                    if (user == null) {
                        throw new UsernameNotFoundException("User not found");
                    }
                    System.out.println("User roles: " + user.getAuthorities());
                    return user;
                });

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/static/**",
                "/img/**",
                "/css/**",
                "/js/**",
                "/webjars/**",
                "/adminPanel"
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}