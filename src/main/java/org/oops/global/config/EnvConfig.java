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
    public void loadEnvVariables() {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
        System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
        System.setProperty("GOOGLE_EMAIL_USERNAME", dotenv.get("GOOGLE_EMAIL_USERNAME"));
        System.setProperty("GOOGLE_EMAIL_PASSWORD", dotenv.get("GOOGLE_EMAIL_PASSWORD"));
        System.setProperty("RDS_HOST", dotenv.get("RDS_HOST"));
        System.setProperty("RDS_USERNAME", dotenv.get("RDS_USERNAME"));
        System.setProperty("RDS_PASSWORD", dotenv.get("RDS_PASSWORD"));
    }
}