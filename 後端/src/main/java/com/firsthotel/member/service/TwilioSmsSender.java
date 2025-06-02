package com.firsthotel.member.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioSmsSender {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    public void sendSms(String toPhoneNumber, String messageText) {
        Twilio.init(accountSid, authToken);

        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),        // 收件人電話
                new PhoneNumber(twilioPhoneNumber),    // Twilio 給你的號碼
                messageText                            // 簡訊內容
        ).create();

        System.out.println("簡訊已發送，SID：" + message.getSid());
    }
    
    
}
