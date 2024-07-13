package study.Spring_Login.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import study.Spring_Login.JWTAuth.JWTUtil;
import study.Spring_Login.Domain.MemberRole;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration configuration;
    private final JWTUtil jwtUtil;
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        // 접근 권한 설정
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/security-login/admin").hasRole(MemberRole.ADMIN.name())
                        .requestMatchers("/security-login/info").authenticated()
                        .anyRequest().permitAll()
                );

        // 폼 로그인 방식 설정
        http
                .formLogin((auth) -> auth.loginPage("/security-login/login")
                        .loginProcessingUrl("/security-login/login")
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/security-login")
                        .failureUrl("/security-login")
                        .permitAll());

        // OAuth 2.0 로그인 방식 설정
        http
                .oauth2Login((auth) -> auth.loginPage("/security-login/login")
                        .defaultSuccessUrl("/security-login")
                        .failureUrl("/security-login/login")
                        .permitAll());

        http
                .logout((auth) -> auth
                        .logoutUrl("/security-login/logout"));

        http
                .csrf((auth) -> auth.disable());

        return http.build();
    }
    /*
    // 스프링 시큐리티 필터 메서드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/security-login").permitAll()
                        .requestMatchers("/security-login/login", "/security-login/join").permitAll()
                        .requestMatchers("/security-login/admin").hasRole(MemberRole.ADMIN.name())
                        .requestMatchers("/security-login/inㅋfo").hasAnyRole(MemberRole.ADMIN.name(), MemberRole.USER.name())
                        .anyRequest().authenticated()
                );
        http
                .logout((auth) -> auth
                        .logoutUrl("/security-login/logout")
                );
        http
                .formLogin((auth) -> auth.loginPage("/security-login/login")
                        .loginProcessingUrl("/security-login/login")
                        .failureUrl("/security-login")
                        .defaultSuccessUrl("/security-login")
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .permitAll()
                );
        http    //csrf 비활성화
                .csrf((auth) -> auth.disable());
        return http.build();
    }
    */
    @Bean //비밀번호 암호화
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

