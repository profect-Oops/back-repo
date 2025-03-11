package org.oops.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * .env 파일 인식할 수 있게 해줌
 */
@Component
public class EnvConfig {

    @PostConstruct
    public void checkEnvVariables() {
        System.out.println("🔍 Checking environment variables:");
        System.out.println("GOOGLE_CLIENT_ID = " + System.getenv("GOOGLE_CLIENT_ID"));
        System.out.println("GOOGLE_CLIENT_SECRET = " + System.getenv("GOOGLE_CLIENT_SECRET"));
        System.out.println("GOOGLE_EMAIL_USERNAME = " + System.getenv("GOOGLE_EMAIL_USERNAME"));
        System.out.println("GOOGLE_EMAIL_PASSWORD = " + System.getenv("GOOGLE_EMAIL_PASSWORD"));
        System.out.println("RDS_HOST = " + System.getenv("RDS_HOST"));
        System.out.println("RDS_USERNAME = " + System.getenv("RDS_USERNAME"));
        System.out.println("RDS_PASSWORD = " + System.getenv("RDS_PASSWORD"));
    }
}