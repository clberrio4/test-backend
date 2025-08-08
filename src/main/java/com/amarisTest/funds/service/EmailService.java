package com.amarisTest.funds.service;

import com.amarisTest.funds.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String defaultFrom;


    @Value("${spring.mail.subject}")
    private String defaultSubject;

    @Async
    private void sendSimpleEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(defaultFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }


    @Async
    public void sendFormattedEmailToClient(Client client, String messageTemplate, Object... args) {
        String fullName = client.getName() + (client.getLastname() != null ? " " + client.getLastname() : "");
        String body = String.format(messageTemplate, args); // <-- primero aplicar format a template
        String content = String.format("Hola %s\n\n%s", fullName, body); // <-- luego insertar en mensaje completo
        sendSimpleEmail(client.getEmail(), defaultSubject, content);
    }
}