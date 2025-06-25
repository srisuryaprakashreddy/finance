package com.project.finance.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendEmail(String to, String subject, String body) {
        // In real app, integrate with SMTP provider
        System.out.println("Sending email to " + to + ": " + subject + " - " + body);
    }
}
