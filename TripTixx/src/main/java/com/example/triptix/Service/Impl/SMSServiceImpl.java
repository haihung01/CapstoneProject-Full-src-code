package com.example.triptix.Service.Impl;

import com.example.triptix.DTO.ResponseObject;
import com.example.triptix.DTO.otp.TwllioKey;
import com.example.triptix.Service.RedisService;
import com.example.triptix.Service.SMSService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SMSServiceImpl implements SMSService {
    @Autowired
    private RedisService redisService;
    //Kha
    @Value("${TWILIO_ACCOUNT_SID}")
    private String ACCOUNT_SID;
    @Value("${TWILIO_AUTH_TOKEN}")
    private String AUTH_TOKEN;
    @Value("${TWILIO_OUTGOING_SMS_NUMBER}")
    private String OUTGOING_SMS_NUMBER;
    //KhaMain - acc premium
    @Value("${TWILIO_ACCOUNT_SID_kha1}")
    private String ACCOUNT_SID_kha1;
    @Value("${TWILIO_AUTH_TOKEN_kha1}")
    private String AUTH_TOKEN_kha1;
    @Value("${TWILIO_OUTGOING_SMS_NUMBER_kha1}")
    private String OUTGOING_SMS_NUMBER_kha1;
    //thuan
    @Value("${TWILIO_ACCOUNT_SID_thuan}")
    private String ACCOUNT_SID_thuan;
    @Value("${TWILIO_AUTH_TOKEN_thuan}")
    private String AUTH_TOKEN_thuan;
    @Value("${TWILIO_OUTGOING_SMS_NUMBER_thuan}")
    private String OUTGOING_SMS_NUMBER_thuan;
    //hung
    @Value("${TWILIO_ACCOUNT_SID_hung}")
    private String ACCOUNT_SID_hung;
    @Value("${TWILIO_AUTH_TOKEN_hung}")
    private String AUTH_TOKEN_hung;
    @Value("${TWILIO_OUTGOING_SMS_NUMBER_hung}")
    private String OUTGOING_SMS_NUMBER_hung;
    //hoang
    @Value("${TWILIO_ACCOUNT_SID_hoang}")
    private String ACCOUNT_SID_hoang;
    @Value("${TWILIO_AUTH_TOKEN_hoang}")
    private String AUTH_TOKEN_hoang;
    @Value("${TWILIO_OUTGOING_SMS_NUMBER_hoang}")
    private String OUTGOING_SMS_NUMBER_hoang;

    @Override
    public ResponseObject<?> sendOTP(String phone) {
        try{
            Random random = new Random();
            int otp = random.nextInt(900000) + 100000;
                //vì nextInt(n) thì sẽ random từ 0 -> n-1 -> 900000 thì sẽ random từ 0 -> 899999
                // + 100000 sse4dam93 bảo kq ta luôn là 6 chữ số, vd: random 1 -> 1 +100000 = 100001 (6 chữ số)
                // random = 899999 -> + 100000 = 999999 (ko vượt quá 6 chữ số )

            //get key
            TwllioKey twllioKey = null;
            switch (phone){
                case "0971724708":
                    twllioKey = getTwllioKha();
                    break;
                case "0961837669":
                    twllioKey = getTwllioThuan();
                    break;
                case "0393829761":
                    twllioKey = getTwllioHung();
                    break;
                case "0708309185":
                    twllioKey = getTwllioHoan();
                    break;
                default:
                    twllioKey = getTwllioKhaPremium();
//                    return ResponseObject.builder().status(false).message("error - must Kha's phone/ Thuận's phone/ Hưng's phone/ Hoàng's phone").build();
            }
            System.out.println("==> twllio key: "+twllioKey.toString());
            //send sms
            Twilio.init(twllioKey.getAccountSid(), twllioKey.getAuthToken());
            String phoneFormatVietNam = "+84" + phone.substring(1);
            String smsMessage = "Your OTP: " + otp + " - Bus Ticket Booking System";
            Message message = Message.creator(
                    new PhoneNumber(phoneFormatVietNam),
                    new PhoneNumber(twllioKey.getOutgoingSmsNumber()),
                    smsMessage
            ).create();
            if(message.getStatus().toString().equals("queued")){
                //send success
                redisService.saveOTPToCacheRedis(phone, otp);
                String msg = "Send otp of " + phone + " : " + otp;
                System.out.println(msg);

                return ResponseObject.builder().status(true).message("send OTP success").build();
            }else{
                //send fail
                return ResponseObject.builder().status(false).message("send OTP fail").build();
            }
        }catch (Exception ex){
            return ResponseObject.builder().status(false).message("error").data(ex.getMessage()).build();
        }
    }

    private TwllioKey getTwllioHoan() {
        return new TwllioKey(ACCOUNT_SID_hoang, AUTH_TOKEN_hoang, OUTGOING_SMS_NUMBER_hoang);
    }

    private TwllioKey getTwllioHung() {
        return new TwllioKey(ACCOUNT_SID_hung, AUTH_TOKEN_hung, OUTGOING_SMS_NUMBER_hung);
    }

    private TwllioKey getTwllioThuan() {
        return new TwllioKey(ACCOUNT_SID_thuan, AUTH_TOKEN_thuan, OUTGOING_SMS_NUMBER_thuan);
    }

    private TwllioKey getTwllioKha() {
        return new TwllioKey(ACCOUNT_SID, AUTH_TOKEN, OUTGOING_SMS_NUMBER);
    }

    private TwllioKey getTwllioKhaPremium() {
        return new TwllioKey(ACCOUNT_SID_kha1, AUTH_TOKEN_kha1, OUTGOING_SMS_NUMBER_kha1);
    }
}
