package com.firsthotel.member.controller;


import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firsthotel.member.bean.Member;
import com.firsthotel.member.bean.MemberRepository;
import com.firsthotel.member.bean.TokenRequest;
import com.firsthotel.member.service.EmailService;
import com.firsthotel.member.service.FirebaseConfig;
import com.firsthotel.member.service.JwtUtil;
import com.firsthotel.member.service.MemberService;
import com.firsthotel.member.service.OtpService;
import com.firsthotel.member.service.TwilioSmsSender;
import com.firsthotel.member.service.logService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("Member")
public class MemberController { 

	@Autowired
	private MemberService mService;
	
	@Autowired
	private MemberRepository mRepository;
	
	 @Autowired
	private PasswordEncoder passwordEncoder;
	 
	 @Autowired
	 private JwtUtil jwtUtil;

	 @Autowired
	 private EmailService emailService;
	 
	 @Autowired
	 private FirebaseConfig firebaseConfig;
	 
	 @Autowired
	 private OtpService otpService;

	 @Autowired
	    private AuthenticationManager authManager;
	 @Autowired
	 private TwilioSmsSender twilioSmsSender;
	 
	 @Autowired
	 private logService logService;
	 
	 
	 
	 @PostMapping("/add")
	    public ResponseEntity<?> addMember(@RequestBody Member member) {
	        try {
	        	 String encodedPassword = passwordEncoder.encode(member.getPassword());
	           member.setPassword(encodedPassword); 
	           member.setRole("user");
	            Member savedMember = mService.insert(member);
	            return ResponseEntity.ok(savedMember);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.badRequest().body(e.getMessage());  
	        }
	    }
	// 軟刪除會員
	    @DeleteMapping("/delete")
	    public ResponseEntity<String> softDeleteMember(@RequestParam(required = false) int memberID) {
	        boolean isSoftDeleted = mService.softDeleteById(memberID);
	        if (isSoftDeleted) {
	            return ResponseEntity.ok("會員已標記為刪除");
	        }
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("會員未找到");
	    }
	
	@GetMapping("/{id}")
    public Optional<Member> getMemberById(@PathVariable Integer id) {
       return mService.findMemberById(id); 
        
    }
	@GetMapping("/search")
	public ResponseEntity<?> searchMembers(
	        @CookieValue(value = "jwt", required = false) String token,
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) Integer phone) {

	    if (token == null || token.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未授權：請先登入");
	    }

	    try {
	        Claims claims = jwtUtil.extractClaims(token);
	        String tokenEmail = claims.getSubject(); // 👈 email
	        String role = claims.get("role", String.class); // 👈 role

	        List<Member> members = mService.findMembers(name, phone, email);
	        
	        for (Member member : members) {
	            member.setOperationLogs(null); // 设置为 null，不返回 operationLogs 字段
	        }
	        
	        return ResponseEntity.ok(members);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT 無效或已過期");
	    }
	}



	 
	
	    @PostMapping("/update/{id}")//後端
	    public Member updateMember(@PathVariable Integer id, @RequestBody Member updatedMember) {
	        
	        Member existingMember = mService.findMemberById(id).orElse(null);
	        if (existingMember != null) {
	           
	            existingMember.setName(updatedMember.getName());
	            existingMember.setPhone(updatedMember.getPhone());
	            existingMember.setEmail(updatedMember.getEmail());
	            existingMember.setBirthDate(updatedMember.getBirthDate());
	            existingMember.setAddress(updatedMember.getAddress());
	            existingMember.setIdCard(updatedMember.getIdCard());
	            existingMember.setPassword(updatedMember.getPassword());
	            existingMember.setRole(updatedMember.getRole());
	           
	           
	            
	           
	            return mService.save(existingMember);
	        }
	        return null;  
	    }
	    @PutMapping("/update")
	    public ResponseEntity<?> updateMember(
	            @RequestBody Member updatedMember,
	            @CookieValue(value = "jwt", defaultValue = "") String token) {
	        
	        if (token.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT Token 缺失");
	        }

	        // 移除 Bearer 前綴，並從 token 中提取使用者 email
	        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));

	        Member member = mRepository.findByEmailIsDeleted(email);
	        if (member == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("會員不存在");
	        }

	        // 更新會員資料
	        member.setName(updatedMember.getName());
	        member.setPhone(updatedMember.getPhone());
	        member.setEmail(updatedMember.getEmail());

	        mRepository.save(member);
	        return ResponseEntity.ok("會員資料已更新");
	    }


	    @GetMapping("/checkEmail")
	    public ResponseEntity<String> checkEmailExistence(@RequestParam String email) {
	        Optional<Member> existingMember = Optional.ofNullable(mRepository.findByEmail(email)); // 使用 Optional.ofNullable
	        if (existingMember.isPresent()) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("此電子郵件已被註冊");
	        }
	        return ResponseEntity.ok("電子郵件可用");
	    }
	    @PostMapping("/checkPassword")
	    public ResponseEntity<String> checkPassword(
	            @RequestBody Map<String, String> payload,
	            @CookieValue(value = "jwt", defaultValue = "") String token) {

	        if (token.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT Token 缺失");
	        }

	        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
	        String inputPassword = payload.get("oldPassword");

	        Member member = mRepository.findByEmailIsDeleted(email);
	        if (member == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("會員不存在");
	        }

	        if (!passwordEncoder.matches(inputPassword, member.getPassword())) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("舊密碼錯誤");
	        }

	        return ResponseEntity.ok("密碼正確");
	    }
	    @PutMapping("/updatePassword")
	    public ResponseEntity<?> updatePassword(
	            @RequestBody Map<String, String> payload,
	            @CookieValue(value = "jwt", defaultValue = "") String token) {

	        if (token.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT Token 缺失");
	        }

	        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));
	        String newPassword = payload.get("newPassword");

	        Member member = mRepository.findByEmailIsDeleted(email);
	        if (member == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("會員不存在");
	        }

	        member.setPassword(passwordEncoder.encode(newPassword));
	        mRepository.save(member);

	        return ResponseEntity.ok("密碼已成功更新");
	    }

	    

	    public String formatPhoneToE164(String phone) {
	        // 假設目前只有台灣用戶
	        if (phone.startsWith("09")) {
	            return "+886" + phone.substring(1);
	        }
	        return phone; // 預設已經是國際格式
	    }
	    @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
	        String email = loginRequest.get("Email");
	        String password = loginRequest.get("password");

	        try {
	            Authentication authentication = authManager.authenticate(
	                new UsernamePasswordAuthenticationToken(email, password)
	            );

	            // 從資料庫查手機號碼
	            Optional<Member> userOpt = mRepository.findByEmail1(email);
	            if (userOpt.isEmpty()) {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("找不到使用者");
	            }

	            String phone = userOpt.get().getPhone(); // 例如 +886912345678
	            String formattedPhone = formatPhoneToE164(phone); // 轉換成 +886xxx
		        otpService.sendOtp(formattedPhone);
	            

	            return ResponseEntity.ok("OTP 已發送至手機：" + phone);
	        } catch (AuthenticationException ex) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("帳號或密碼錯誤");
	        }
	    }
	    
	    @PostMapping("/verify-otp")
	    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> otpRequest) {
	        String email = otpRequest.get("Email");
	        String otp = otpRequest.get("otp");

	        // 驗證 OTP
	        Optional<Member> userOpt = mRepository.findByEmail1(email);
	        if (userOpt.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("找不到使用者");
	        }

	        String phone = userOpt.get().getPhone(); // 取得手機號碼
	        String formattedPhone = formatPhoneToE164(phone); // 轉換成 +886xxx
	       String role=userOpt.get().getRole();
	        if (otpService.verifyOtp(formattedPhone, otp)) {
	            // 如果 OTP 驗證成功，產生 JWT
	            String jwt = jwtUtil.generateToken(email,role);
	            
	            
	            ResponseCookie jwtCookie=ResponseCookie.from("jwt",jwt)
	            		.httpOnly(false)
	            		.secure(false)
	            		.path("/")
	            		.maxAge(Duration.ofHours(1))
	            		.sameSite("Strict")
	            		.build();

	            return ResponseEntity.ok()
	            		 .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
	            		.body(Map.of("message","OTP驗證成功")); // 假設你有封裝 JWT 回應的類別
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP 驗證失敗");
	        }
	    }
	    @PostMapping("/logout")
	    public ResponseEntity<?> logout(HttpServletResponse response) {
	        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
	                .httpOnly(false)
	                .secure(false) // 如果你部署在 https 記得改成 true
	                .path("/")
	                .maxAge(0) // 設為 0 表示馬上過期
	                .sameSite("Strict")
	                .build();

	        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
	        return ResponseEntity.ok(Map.of("message", "已成功登出"));
	    }

	  

		@GetMapping("/validate")
	    public String validateToken(@RequestParam String token) {
	        String username = jwtUtil.extractUsername(token);
	        if (jwtUtil.validateToken(token, username)) {
	            return "Token is valid";
	        } else {
	            return "Token is invalid or expired";
	        }
	    }
	    @PostMapping("/someProtectedResource")
	    public ResponseEntity<?> getProtectedResource(@RequestHeader("Authorization") String authorizationHeader) {
	        
	        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization header must be Bearer token");
	        }

	        String token = authorizationHeader.substring(7);

	       
	        if (jwtUtil.isTokenExpired(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has expired");
	        }

	      
	        String username = jwtUtil.extractUsername(token);

	        
	        return ResponseEntity.ok("Protected resource accessed by " + username);
	    }


	    @GetMapping("/profile")
	    public ResponseEntity<?> getProfile(@CookieValue("jwt") String jwt) {
	        try {
	            String email = jwtUtil.extractUsername(jwt);
	            Member member = mRepository.findByEmail(email);
	            return ResponseEntity.ok(member);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("無效Token");
	        }
	    }


	    

	    
	    @PostMapping("/forgot-password")
	    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
	        String email = body.get("email");
	        Member member = mRepository.findByEmail(email);

	        if (member == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
	        }

	        String token = UUID.randomUUID().toString();
	        member.setResetToken(token);
	        member.setResetTokenExpiration(LocalDateTime.now().plusHours(1));
	        mRepository.save(member);

	        String resetLink = "http://localhost:5173/resetpassword?token=" + token;
	        emailService.send(email, "重設密碼連結", "請點擊以下連結以重設密碼: " + resetLink);

	        return ResponseEntity.ok("郵件已寄出");
	    }

	    @PostMapping("/reset-password")
	    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
	        String token = body.get("token");
	        String newPassword = body.get("newPassword");

	        // 1. 找出擁有這個 token 的會員
	        Member member = mRepository.findByResetToken(token);
	        if (member == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("無效的重設密碼連結");
	        }

	        // 2. 驗證 token 是否過期
	        if (member.getResetTokenExpiration() == null || member.getResetTokenExpiration().isBefore(LocalDateTime.now())) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("連結已過期，請重新申請");
	        }

	        // 3. 更新密碼
	        member.setPassword(passwordEncoder.encode(newPassword));

	        // 4. 清除 token
	        member.setResetToken(null);
	        member.setResetTokenExpiration(null);

	        mRepository.save(member);

	        return ResponseEntity.ok("密碼已成功更新！");
	    }
	   
	    @GetMapping("/exportLogs")
	    public ResponseEntity<byte[]> exportLogs() {
	        try {
	            String csvContent ="\uFEFF" + logService.generateLogsCsv();

	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.parseMediaType("text/csv"));
	            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=logs.csv");

	            return ResponseEntity.ok()
	                    .headers(headers)
	                    .body(csvContent.getBytes(StandardCharsets.UTF_8));

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body(("產生Log失敗：" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
	        }
	    }

}
