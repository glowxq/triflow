package com.glowxq.common.redis;

import com.glowxq.common.core.common.constant.GlobalConstant;
import com.glowxq.common.redis.listener.UserPermissionChangeListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * Redis 消息订阅配置
 * <p>
 * 配置 Redis 消息监听器，用于处理用户权限变更等消息。
 * </p>
 *
 * @author glowxq
 * @version 1.1
 * @since 2023/9/8
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "redis.listener.enable", havingValue = "true")
public class RedisMessageConfiguration {

    private final RedisConnectionFactory redisConnectionFactory;

    private final UserPermissionChangeListener userPermissionChangeListener;

    /**
     * 配置消息监听容器
     */
    @Bean
    public RedisMessageListenerContainer container() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        // 订阅用户权限变更通道
        PatternTopic permissionChangeTopic = new PatternTopic(GlobalConstant.CHANGE_PERMISSIONS_SIGNAL);
        container.addMessageListener(userPermissionChangeListener, permissionChangeTopic);
        return container;
    }

}
