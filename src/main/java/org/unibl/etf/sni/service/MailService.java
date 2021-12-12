package org.unibl.etf.sni.service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingFormatArgumentException;
import java.util.Properties;

public class MailService {

    final static String username;
    final static String password;

    static{
        Properties prop = ConfigListener.getProperties();
        username = prop.getProperty("username");
        password = prop.getProperty("password");
        if(username==null || password==null)
            throw new MissingFormatArgumentException("Config file is not correct!");
    }

    public static void sendEmail(String to, String title, String content, InputStream is, String filename) throws IOException, MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username, "SNI_CHAT"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(title);
        message.setContent(content,"text/plain");

        if (is != null) {
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(content);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            BodyPart messageBodyPart2 = new MimeBodyPart();
            messageBodyPart2.setDataHandler(new DataHandler((DataSource) is));
            messageBodyPart2.setFileName(filename);
            multipart.addBodyPart(messageBodyPart2);

            message.setContent(multipart);
        }
        Transport.send(message);

    }

}