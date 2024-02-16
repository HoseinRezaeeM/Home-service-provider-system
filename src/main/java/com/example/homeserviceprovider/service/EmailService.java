package com.example.homeserviceprovider.service;


import com.example.homeserviceprovider.domain.user.enums.Role;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {


    void sendEmail(SimpleMailMessage email);

    SimpleMailMessage createEmail(String toEmail,String firstName, String confirmationToken, Role role);

}
