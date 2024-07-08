package study.Spring_Login.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import study.Spring_Login.DTO.JWTFilter;
import study.Spring_Login.DTO.JWTUtil;
import study.Spring_Login.DTO.LoginFilter;
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
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // 스프링 시큐리티 jwt 로그인 설정
        // csrf disable 설정
        http
                .csrf((auth) -> auth.disable());
        // 폼로그인 형식 disable 설정 => POSTMAN으로 검증할 것임!
        http
                .formLogin((auth) -> auth.disable());
        // http basic 인증 방식 disable 설정
        http
                .httpBasic((auth -> auth.disable()));
        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/jwt-login", "/jwt-login/", "/jwt-login/login", "/jwt-login/join").permitAll()
                        .requestMatchers("/jwt-login/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        // 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 새로 만든 로그인 필터를 원래의 (UsernamePasswordAuthenticationFilter)의 자리에 넣음
        http
                .addFilterAt(new LoginFilter(authenticationManager(configuration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
        // 로그인 필터 이전에 JWTFilter를 넣음
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        return http.build();
    }
    */
    /*
    // 스프링 시큐리티 필터 메서드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/security-login").permitAll()
                        .requestMatchers("/security-login/login", "/security-login/join").permitAll()
                        .requestMatchers("/security-login/admin").hasRole(MemberRole.ADMIN.name())
                        .requestMatchers("/security-login/info").hasAnyRole(MemberRole.ADMIN.name(), MemberRole.USER.name())
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

