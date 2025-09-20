package com.entreprise.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;

import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;
    
    @Value("${spring.mail.port:587}")
    private int mailPort;
    
    @Value("${spring.mail.username:}")
    private String mailUsername;
    
    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Bean
    @ConditionalOnProperty(name = "mail.mode", havingValue = "real", matchIfMissing = true)
    public JavaMailSender realMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // Configurer les propriétés depuis application.properties
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.debug", "false");
        
        return mailSender;
    }
    
    @Bean
    @ConditionalOnProperty(name = "mail.mode", havingValue = "mock")
    public JavaMailSender mockMailSender() {
        return new MockJavaMailSender();
    }
    
    // Implementation mock pour les tests
    public static class MockJavaMailSender implements JavaMailSender {
        
        @Override
        public void send(SimpleMailMessage simpleMessage) throws org.springframework.mail.MailException {
            System.out.println("=== EMAIL MOCK ===");
            System.out.println("To: " + java.util.Arrays.toString(simpleMessage.getTo()));
            System.out.println("Subject: " + simpleMessage.getSubject());
            System.out.println("Text: " + simpleMessage.getText());
            System.out.println("==================");
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) throws org.springframework.mail.MailException {
            for (SimpleMailMessage message : simpleMessages) {
                send(message);
            }
        }

        @Override
        public MimeMessage createMimeMessage() {
            System.out.println("Creating mock MimeMessage");
            return null;
        }

        @Override
        public MimeMessage createMimeMessage(InputStream contentStream) throws org.springframework.mail.MailException {
            System.out.println("Creating mock MimeMessage from InputStream");
            return null;
        }

        @Override
        public void send(MimeMessage mimeMessage) throws org.springframework.mail.MailException {
            System.out.println("=== MIME EMAIL MOCK ===");
            System.out.println("MimeMessage sent (mock)");
            System.out.println("=======================");
        }

        @Override
        public void send(MimeMessage... mimeMessages) throws org.springframework.mail.MailException {
            for (MimeMessage message : mimeMessages) {
                send(message);
            }
        }
    }
}
