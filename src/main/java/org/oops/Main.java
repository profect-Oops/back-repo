package org.oops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.filter.ForwardedHeaderFilter;

@SpringBootApplication  // 스프링 부트 애플리케이션으로 설정
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args); //스프링 부트 실행
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("None"); // cross-site 요청 허용
        serializer.setUseSecureCookie(true); // HTTPS에서만 전송
        return serializer;
    }

}
