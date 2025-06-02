package com.firsthotel.member.service;

import org.springframework.stereotype.Service;

import com.firsthotel.member.bean.Member;
import com.firsthotel.member.bean.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public Integer getMemberIdFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        Member member = memberRepository.findByEmail(username);
        if (member != null) {
            return member.getMemberID();
        }
        return null;  // 或者丟 exception，看你想怎麼處理
    }
}
