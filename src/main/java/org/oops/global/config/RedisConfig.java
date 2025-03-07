package org.oops.global.config;
import org.oops.api.coin.dto.CoinCandleDTO;
import org.oops.api.redis.service.PriceUpdateSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.List;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(redisConfig);
    }

    // 기존 CoinCandleDTO 저장용 RedisTemplate
    @Bean
    public RedisTemplate<String, List<CoinCandleDTO>> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<CoinCandleDTO>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());  // JSON 직렬화 사용
        return template;
    }

    // 가격 업데이트 메시지 저장용 RedisTemplate (Pub/Sub 용도)
    @Bean
    public RedisTemplate<String, String> redisStringTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    // Redis Pub/Sub 토픽 (코인 가격 업데이트)
    @Bean
    public ChannelTopic priceUpdateTopic() {
        return new ChannelTopic("price-update");
    }

    // Redis 메시지 리스너 컨테이너
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter messageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener, priceUpdateTopic());
        return container;
    }

    // Redis Pub/Sub 메시지 리스너
    @Bean
    public MessageListenerAdapter messageListener(PriceUpdateSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }
}
