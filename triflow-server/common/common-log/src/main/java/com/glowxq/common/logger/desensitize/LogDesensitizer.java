package com.glowxq.common.logger.desensitize;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.glowxq.common.logger.utils.DesensitizationUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * 日志脱敏处理器
 * <p>
 * 通过反射修改 LoggingEvent 中的消息内容，实现日志脱敏功能。
 * </p>
 *
 * @author glowxq
 * @since 2021/1/9
 */
@Slf4j
public final class LogDesensitizer {

    private static final String MESSAGE_FIELD = "message";
    private static final String FORMATTED_MESSAGE_FIELD = "formattedMessage";

    /**
     * 脱敏工具实例（线程安全，可复用）
     */
    private static final DesensitizationUtil DESENSITIZATION_UTIL = new DesensitizationUtil();

    private LogDesensitizer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 对日志事件进行脱敏处理
     *
     * @param event 日志事件
     */
    public static void desensitize(LoggingEvent event) {
        if (event == null || event.getArgumentArray() == null) {
            return;
        }

        String originalMessage = event.getFormattedMessage();
        String desensitizedMessage = DESENSITIZATION_UTIL.customChange(originalMessage);

        if (desensitizedMessage == null || desensitizedMessage.isEmpty()) {
            return;
        }

        try {
            Class<? extends LoggingEvent> eventClass = event.getClass();

            Field messageField = eventClass.getDeclaredField(MESSAGE_FIELD);
            messageField.setAccessible(true);
            messageField.set(event, desensitizedMessage);

            Field formattedMessageField = eventClass.getDeclaredField(FORMATTED_MESSAGE_FIELD);
            formattedMessageField.setAccessible(true);
            formattedMessageField.set(event, desensitizedMessage);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.error("日志脱敏处理失败", e);
        }
    }
}
