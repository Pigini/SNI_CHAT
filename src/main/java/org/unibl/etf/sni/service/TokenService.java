package org.unibl.etf.sni.service;

import org.unibl.etf.sni.model.Token;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenService {

    private static final HashMap<String, Token> map = new HashMap<>();
    private static final int DURATION_MINUTES = 5;


    public static void generateToken(String username, String email, String phoneNumber) throws MessagingException {
        String token = generateRandomToken();
        String partToMail = token.substring(0,token.length()-6);
        String partToSMS = token.substring(token.length()-6);

        map.put(username, new Token(token));
        try {
            MailService.sendEmail(email, "Token for SNI_CHAT",
                    "Copy the following first part of token to your sign in page: " + partToMail + "."
                            +username,
                    null, null);

            //
            SmsService.sendSMS(phoneNumber,"Copy the following second part of token to your sign in page: "+partToSMS);
            //
        } catch (IOException e) {
            Logger.getLogger(TokenService.class.getName()).log(Level.SEVERE, e.toString());
        }
    }

    public static boolean checkToken(String username, String token) {
        Token tokenObj = map.get(username);
        if (tokenObj != null) {
            return token.equals(tokenObj.getToken()) && Token.now().toInstant().isAfter(tokenObj.getDateTimeCreated().toInstant()
                    .plus(DURATION_MINUTES,ChronoUnit.MINUTES));
        }
        return false;
    }


    private static String generateRandomToken() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-","");
    }

}
