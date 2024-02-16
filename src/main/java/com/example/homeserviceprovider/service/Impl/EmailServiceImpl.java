package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.domain.user.enums.Role;
import com.example.homeserviceprovider.service.EmailService;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl implements EmailService {

      private final JavaMailSender mailSender;

      public EmailServiceImpl(JavaMailSender mailSender) {
            this.mailSender = mailSender;
      }


      @Override
      public SimpleMailMessage createEmail(String toEmail, String firstName, String token, Role role) {
            String accountType = role.name();
            String emailText = "Hi " + firstName +
                               """
                                                      
                                      You registered an account on [HOME*SERVICE*PROVIDER]
                                      before being able to use your account you need to verify that this is your 
                                      email address by clicking below link.
                                                      
                                      NOTE: The link is valid for 15 minutes and expires after the specified time.
                                                      
                                                                    
                                      link:http://localhost:8080/registration/confirm?token=""" + token +
                               """
                                        
                                                                        
                                      We're at your service :)
                                      """;
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setFrom("rezaeen77@gmail.com");
            mailMessage.setSubject("Complete " + accountType + " Registration!");
            mailMessage.setText(emailText);
            return mailMessage;
      }

      @Override
      public void sendEmail(SimpleMailMessage email) {
            mailSender.send(email);
      }
}
