package com.firsthotel.member.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.firsthotel.member.bean.Member;
import com.firsthotel.member.bean.MemberRepository;
import com.firsthotel.member.bean.OperationLog;
import com.firsthotel.member.bean.OperationLogRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private OperationLogRepository operationLogRepository;
	
	@Autowired
    private HttpServletRequest request; 

	
	private Long getCurrentOperatorId() 
	{
		String authHeader = request.getHeader("Authorization");
		if(authHeader !=null && authHeader.startsWith("Bearer ")) 
		{
			String token = authHeader.substring(7);
			String username = jwtUtil.extractUsername(token); 
			Optional<Member> memberOpt = memberRepository.findByEmail1(username); // 這邊假設username是email
	        if (memberOpt.isPresent()) {
	            return (long)memberOpt.get().getMemberID(); // Member的id轉成Long
	        }
	        
		}
		return null;
	}
	
	private void logOperation(String action, String description, Member targetMember) {
        OperationLog log = new OperationLog();
        log.setOperatorId(getCurrentOperatorId()); // 自動從token取
        log.setAction(action);
        log.setDescription(description);
        log.setIpAddress(request.getRemoteAddr()); // 自動抓操作IP
        log.setCreatedAt(LocalDateTime.now());
        log.setMember(targetMember); // 關聯到影響到的會員
        operationLogRepository.save(log);
    }
	
	
	public Optional<Member> findMemberById(Integer id) {
		return memberRepository.findById(id);
	}

	// 新增會員
	public Member insert(Member insertBean) {
		if (insertBean != null) {
			insertBean.setJoinDate(new Date());
			Member saved = memberRepository.save(insertBean);
			logOperation("CREATE_MEMBER", "Created new member: " + saved.getEmail(), saved);
			return saved;
		}
		return null;
	}

	

	public boolean softDeleteById(int memberId) {
		Optional<Member> memberOpt = memberRepository.findById(memberId);
		if (memberOpt.isPresent()) {
			Member member = memberOpt.get();
			member.setIsDeleted(1); 
			member.setDeletedAt(new Date()); 
			memberRepository.save(member);
			
			
			logOperation("DELETE_MEMBER", "Soft deleted member ID: " + memberId, member);
			return true;
		}
		return false;
	}

	// 多模糊查詢(測試成功)
	public List<Member> findMembers(String name, Integer phone, String email) {
		MemberSpecification spec = new MemberSpecification(name, phone, email);
		return memberRepository.findAll(spec);
	}

	public Member save(Member member) {
		
		Member updated = memberRepository.save(member);
		logOperation("UPDATE_MEMBER", "Updated member: " + updated.getEmail(), updated);
		return updated;
	}

}
