package com.glowxq.triflow.base.auth.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.redis.RedisCache;
import com.glowxq.common.sms.SmsTemplate;
import com.glowxq.common.sms.model.SmsResult;
import com.glowxq.triflow.base.auth.config.SmsCodeProperties;
import com.glowxq.triflow.base.auth.config.SmsProviderProperties;
import com.glowxq.triflow.base.auth.enums.SmsProviderEnum;
import com.glowxq.triflow.base.auth.service.SmsCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * 短信验证码服务实现
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsCodeServiceImpl implements SmsCodeService {

    private final RedisCache redisCache;
    private final SmsTemplate smsTemplate;
    private final SmsCodeProperties smsCodeProperties;
    private final SmsProviderProperties smsProviderProperties;
    private final AliyunPnvsSmsService aliyunPnvsSmsService;

    /**
     * 短信配置标识（对应 sms.yml 中的 blends.autoLogin）
     */
    private static final String SMS_CONFIG_ID = "autoLogin";

    /**
     * 验证码有效期（秒）
     */
    private static final long CODE_EXPIRE_SECONDS = 300;

    /**
     * 发送间隔（秒）
     */
    private static final long SEND_INTERVAL_SECONDS = 60;

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 模板参数键
     */
    private static final String TEMPLATE_PARAM_CODE = "code";
    private static final String TEMPLATE_PARAM_MIN = "min";

    @Override
    public void sendCode(String phone, String type) {
        // 检查发送频率
        if (!redisCache.canSendSms(phone, type, SEND_INTERVAL_SECONDS)) {
            throw new BusinessException("发送过于频繁，请稍后再试");
        }

        // 生成验证码
        String code = generateCode(CODE_LENGTH);

        if (!smsCodeProperties.isSupportedType(type)) {
            throw new BusinessException("验证码类型不支持");
        }

        String templateId = smsCodeProperties.resolveTemplateId(type);
        if (templateId == null || templateId.isBlank()) {
            throw new BusinessException("短信模板未配置");
        }

        Map<String, String> params = new LinkedHashMap<>();
        params.put(TEMPLATE_PARAM_CODE, code);
        params.put(TEMPLATE_PARAM_MIN, String.valueOf(CODE_EXPIRE_SECONDS / 60));

        SmsProviderEnum provider = SmsProviderEnum.of(smsProviderProperties.getProvider());
        if (provider == null) {
            throw new BusinessException("短信服务商未配置");
        }

        SmsResult result = switch (provider) {
            case SMS4J -> smsTemplate.sendTemplate(SMS_CONFIG_ID, phone, templateId, params);
            case ALIYUN_PNVS -> aliyunPnvsSmsService.sendVerifyCode(phone, templateId, params);
        };

        if (result == null || !result.isSuccess()) {
            String errorCode = result != null ? result.getErrorCode() : null;
            String errorMessage = result != null ? result.getMessage() : null;
            Object rawResponse = result != null ? result.getRawResponse() : null;
            log.error("发送短信验证码失败, phone={}, type={}, templateId={}, errorCode={}, error={}, raw={}",
                    maskPhone(phone), type, templateId, errorCode, errorMessage, rawResponse);
            throw new BusinessException("发送短信失败，请稍后重试");
        }

        if (provider == SmsProviderEnum.ALIYUN_PNVS
                && Boolean.TRUE.equals(smsProviderProperties.getPnvs().getReturnVerifyCode())
                && result.getRawResponse() != null) {
            String verifiedCode = aliyunPnvsSmsService.extractVerifyCode(result.getRawResponse());
            if (StringUtils.hasText(verifiedCode)) {
                code = verifiedCode;
            }
        }

        // 保存验证码到 Redis
        redisCache.putSmsCode(phone, type, code, CODE_EXPIRE_SECONDS);

        // 设置发送限制
        redisCache.setSmsLimit(phone, type, SEND_INTERVAL_SECONDS);

        log.info("发送短信验证码成功, phone={}, type={}", maskPhone(phone), type);
    }

    @Override
    public boolean verifyCode(String phone, String type, String code) {
        return redisCache.verifySmsCode(phone, type, code);
    }

    /**
     * 生成数字验证码
     *
     * @param length 验证码长度
     * @return 验证码
     */
    private String generateCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }


    /**
     * 手机号脱敏
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

}
