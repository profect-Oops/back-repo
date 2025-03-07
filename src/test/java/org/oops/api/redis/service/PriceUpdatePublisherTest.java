//package org.oops.api.redis.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mockito;
//import org.oops.api.coin.dto.CoinPriceDTO;
//import org.oops.api.email.service.EmailService;
//import org.oops.domain.alert.Alert;
//import org.oops.domain.alert.AlertRepository;
//import org.springframework.data.redis.connection.Message;
//
//import java.math.BigDecimal;
//import java.nio.charset.StandardCharsets;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class PriceUpdateSubscriberTest {
//
//    private PriceUpdateSubscriber subscriber;
//    private AlertRepository alertRepository;
//    private EmailService emailService;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        alertRepository = mock(AlertRepository.class);
//        emailService = mock(EmailService.class);
//        objectMapper = new ObjectMapper();
//        subscriber = new PriceUpdateSubscriber(alertRepository, emailService);
//    }
//
//    @Test
//    void testOnMessage_PriceExceedsAlert_ShouldSendEmail() throws Exception {
//        // Given: 사용자가 설정한 가격이 50000원, 현재 가격이 52000원
//        CoinPriceDTO priceDTO = CoinPriceDTO.builder()
//                .price(52000.0)
//                .code("KRW-BTC")
//                .build();
//        String messageJson = objectMapper.writeValueAsString(priceDTO);
//        Message message = new Message(messageJson.getBytes(StandardCharsets.UTF_8), null);
//
//        Alert alert = new Alert(
//                BigDecimal.valueOf(50000),
//                true,  // 활성화 상태
//                null,
//                null,
//                "비트코인",
//                "KRW-BTC",
//                "test@example.com"
//        );
//
//        when(alertRepository.findByCoinTickerAndAlertActiveTrue("KRW-BTC"))
//                .thenReturn(List.of(alert));
//
//        // When: Redis 메시지가 구독됨
//        subscriber.onMessage(message, null);
//
//        // Then: 이메일이 전송되었는지 확인
//        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);
//
//        verify(emailService, times(1))
//                .sendEmailNotice(emailCaptor.capture(), subjectCaptor.capture(), contentCaptor.capture());
//
//        assertEquals("test@example.com", emailCaptor.getValue());
//        assertEquals("코인 가격 알림", subjectCaptor.getValue());
//        assertTrue(contentCaptor.getValue().contains("52000"));
//        assertTrue(contentCaptor.getValue().contains("비트코인"));
//
//        // Then: alertActive가 false로 변경되었는지 확인
//        assertFalse(alert.getAlertActive());
//        verify(alertRepository, times(1)).save(alert);
//    }
//
//    @Test
//    void testOnMessage_PriceBelowAlert_ShouldNotSendEmail() throws Exception {
//        // Given: 사용자가 설정한 가격이 50000원, 현재 가격이 48000원
//        CoinPriceDTO priceDTO = CoinPriceDTO.builder()
//                .price(52000.0)
//                .code("KRW-BTC")
//                .build();
//        String messageJson = objectMapper.writeValueAsString(priceDTO);
//        Message message = new Message(messageJson.getBytes(StandardCharsets.UTF_8), null);
//
//        Alert alert = new Alert(
//                BigDecimal.valueOf(50000),
//                true,
//                null,
//                null,
//                "비트코인",
//                "KRW-BTC",
//                "test@example.com"
//        );
//
//        when(alertRepository.findByCoinTickerAndAlertActiveTrue("KRW-BTC"))
//                .thenReturn(List.of(alert));
//
//        // When: Redis 메시지가 구독됨
//        subscriber.onMessage(message, null);
//
//        // Then: 이메일이 전송되지 않아야 함
//        verify(emailService, never()).sendEmailNotice(any(), any(), any());
//        verify(alertRepository, never()).save(any());
//    }
//}