package com.glowxq.common.logger.desensitize;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;

/**
 * 支持脱敏的滚动文件日志输出器
 * <p>
 * 在输出日志前对敏感信息进行脱敏处理。
 * </p>
 *
 * @author glowxq
 * @since 2021/1/9
 */
public class DesensitizeRollingFileAppender extends RollingFileAppender<LoggingEvent> {

    @Override
    protected void subAppend(LoggingEvent event) {
        LogDesensitizer.desensitize(event);
        super.subAppend(event);
    }
}
