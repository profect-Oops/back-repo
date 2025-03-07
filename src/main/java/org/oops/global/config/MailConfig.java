package org.oops.global.config;

import lombok.RequiredArgsConstructor;
import org.oops.api.email.properties.MailProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 스프링에서 메일 전송을 위한 클래스
 */
@Configuration
@RequiredArgsConstructor
public class MailConfig {
    private final MailProperties mailProperties;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        Properties properties = mailSender.getJavaMailProperties();

        // 속성 순서 중요! STARTTLS 설정을 최우선으로 선언
        properties.put("mail.smtp.starttls.enable", "true");  // TLS 사용 필수
        properties.put("mail.smtp.starttls.required", "true");  // TLS 강제
        properties.put("mail.smtp.auth", "true");  // 인증 활성화
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");  // Gmail 서버 신뢰
        properties.put("mail.smtp.port", "587");  // Gmail SMTP 포트
        properties.put("mail.transport.protocol", "smtp");  // SMTP 프로토콜 사용

        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }
}