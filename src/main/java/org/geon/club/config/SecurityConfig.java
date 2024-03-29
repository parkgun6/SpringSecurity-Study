package org.geon.club.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.geon.club.security.filter.ApiCheckFilter;
import org.geon.club.security.filter.ApiLoginFailHandler;
import org.geon.club.security.filter.ApiLoginFilter;
import org.geon.club.security.handler.ClubLoginSuccessHandler;
import org.geon.club.security.service.ClubUserDetailService;
import org.geon.club.security.util.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {

    private final ClubUserDetailService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/sample/all").permitAll()
//                .antMatchers("/sample/member").hasRole("USER")
//                .antMatchers("/sample/admin").hasRole("ADMIN");

        http.formLogin().defaultSuccessUrl("/sample/member");
        http.csrf().disable(); // get방식으로 logout이 가능함
        http.logout();

        http.oauth2Login().successHandler(successHandler());
        http.rememberMe().tokenValiditySeconds(60*60*7)
                .userDetailsService(userDetailsService);

        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        // 필터의 동작순서를 변경한다.
        http.addFilterBefore(apiCheckFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(apiLoginFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public ClubLoginSuccessHandler successHandler() {
        return new ClubLoginSuccessHandler(passwordEncoder());
    }

    @Bean
    public ApiCheckFilter apiCheckFilter() {
        return new ApiCheckFilter("/notes/**/*", jwtUtil());
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public ApiLoginFilter apiLoginFilter(AuthenticationManager authenticationManager) throws Exception {
        ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/api/login", jwtUtil());
        apiLoginFilter.setAuthenticationManager(authenticationManager);

        apiLoginFilter.setAuthenticationFailureHandler(new ApiLoginFailHandler());
        return apiLoginFilter;
    }

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil();
    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User
//                .withUsername("user1")
//                .password("$2a$10$3Z3kG1jHJo2qpFI5zVse3.CDDBECGcrZH81Q5/bR7cXr.dsZ9SjeW")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

}
