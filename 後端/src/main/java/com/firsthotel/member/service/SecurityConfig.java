package com.firsthotel.member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.firsthotel.member.bean.Member;
import com.firsthotel.member.bean.MemberRepository;

@Configuration
public class SecurityConfig {

	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
	 @Autowired
	    private MemberRepository memberRepository;
	@Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Member member = memberRepository.findByEmail1(username)
                .orElseThrow(() -> new UsernameNotFoundException("使用者不存在: " + username));

            return new org.springframework.security.core.userdetails.User(
                member.getEmail(),
                member.getPassword(), // 確保你存在資料庫的密碼是 encode 過的！
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        };
    }
	
	
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 加密器 OK
    }

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 所有請求都允許
            )
            .oauth2Login(oauth -> oauth
                .successHandler(oAuth2LoginSuccessHandler)
            )
            .logout().permitAll();

        return http.build();
    }

    
    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.cors().and()
//            .csrf().disable()
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/Member/**").permitAll() // 放行你原本的 Member API
//                .anyRequest().authenticated()
//            )
//            .oauth2Login(oauth -> oauth
//                .successHandler(oAuth2LoginSuccessHandler) // 登入成功導向
//            )
//            .logout().permitAll();
//
//        return http.build();
//    }
}
