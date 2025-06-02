package com.firsthotel.member.service;

import com.firsthotel.member.bean.Member;
import com.firsthotel.member.bean.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final MemberRepository memberRepository;
	private final JwtUtil jwtUtil;

	public OAuth2LoginSuccessHandler(MemberRepository memberRepository, JwtUtil jwtUtil) {
		this.memberRepository = memberRepository;
		this.jwtUtil = jwtUtil;
	}

	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String phone=oAuth2User.getAttribute("phone");

        Member member = memberRepository.findByEmail(email);
        if (member == null ) {
            // 新用戶，建立帳號
            member = new Member();
            member.setEmail(email);
            member.setName(name);
            member.setPhone(phone);
            member.setRole("user");
            member.setPassword(""); // 無密碼登入
            memberRepository.save(member);
        } else if (member.getIsDeleted() == 1) {
            // 使用者被軟刪除，阻擋登入（你可以改這裡的處理方式）
        	response.sendRedirect("http://localhost:5173/AccountDisabled");
            
            return;
        }

        String token = jwtUtil.generateToken(email, member.getRole());

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                .httpOnly(true) // 防止 JavaScript 存取，提高安全性
                .secure(false) // 如果是 HTTPS 記得改成 true
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Strict")
                .build();

        response.setHeader("Set-Cookie", jwtCookie.toString());
        System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());
        // 乾淨地重導到前端頁面
        response.sendRedirect("http://localhost:5173/loginsuccess?token=" + token);


    }
}
