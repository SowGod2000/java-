package com.firsthotel.member.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    @Autowired
    private TwilioSmsSender smsSender;

    private final Map<String, String> otpMap = new ConcurrentHashMap<>();

    public void sendOtp(String phoneNumber) {
        String otp = generateOtp();
        otpMap.put(phoneNumber, otp);

        String message = "您的驗證碼是：" + otp + "，請於 5 分鐘內完成驗證";
        smsSender.sendSms(phoneNumber, message);
        System.out.println(phoneNumber+"\t"+ message);
    }

    public boolean verifyOtp(String phoneNumber, String inputOtp) {
        return inputOtp != null && inputOtp.equals(otpMap.get(phoneNumber));
    }

    private String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}
