package org.unibl.etf.sni.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsService {

    public static final String ACCOUNT_SID = "AC8ddd231e07df373600256380fefe1304";
    public static final String AUTH_TOKEN = "7c9307ba8165e64b520a22c55252670a";
    public static final String MY_TWILIO_PHONE_NUMBER = "+17252344847";
    public static void sendSMS(String phoneNumber,String content){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(new PhoneNumber(phoneNumber),
                new PhoneNumber(MY_TWILIO_PHONE_NUMBER), content).create();
    }

}
