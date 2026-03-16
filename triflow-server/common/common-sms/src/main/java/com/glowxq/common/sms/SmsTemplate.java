package com.glowxq.common.sms;

import com.glowxq.common.sms.enums.SmsErrorCode;
import com.glowxq.common.sms.model.SmsResult;
import com.glowxq.common.sms.util.PhoneUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信发送模板
 * <p>
 * 基于 SMS4J 封装的统一短信发送服务，支持多渠道短信发送。
 * </p>
 *
 * <h3>支持的短信渠道</h3>
 * <ul>
 *     <li>阿里云短信</li>
 *     <li>腾讯云短信</li>
 *     <li>华为云短信</li>
 *     <li>七牛云短信</li>
 *     <li>云片短信</li>
 *     <li>网易云信</li>
 *     <li>等更多...</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 发送验证码短信
 * SmsResult result = smsTemplate.sendCode("aliyun", "13800138000", "123456");
 *
 * // 发送模板短信
 * Map<String, String> params = Map.of("code", "123456", "expireTime", "5");
 * SmsResult result = smsTemplate.sendTemplate("aliyun", "13800138000", "SMS_001", params);
 *
 * // 批量发送
 * List<String> phones = List.of("13800138001", "13800138002");
 * List<SmsResult> results = smsTemplate.batchSend("aliyun", phones, "SMS_001", params);
 * }</pre>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsTemplate {

    /**
     * 发送短信验证码
     * <p>
     * 使用配置中指定的默认模板发送验证码。
     * </p>
     *
     * @param configId 配置标识（在 sms.yml 中定义的自定义标识）
     * @param phone    手机号
     * @param code     验证码
     *
     * @return 发送结果
     */
    public SmsResult sendCode(String configId, String phone, String code) {
        try {
            SmsBlend smsBlend = getSmsBlend(configId);
            SmsResponse response = smsBlend.sendMessage(phone, code);
            return buildResult(configId, phone, response);
        } catch (Exception e) {
            log.error("发送短信验证码失败，configId: {}, phone: {}", configId, PhoneUtils.mask(phone), e);
            return SmsResult.fail(configId, phone, SmsErrorCode.SEND_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 发送短信验证码（使用默认配置）
     *
     * @param phone 手机号
     * @param code  验证码
     *
     * @return 发送结果
     */
    public SmsResult sendCode(String phone, String code) {
        try {
            SmsBlend smsBlend = SmsFactory.getSmsBlend();
            SmsResponse response = smsBlend.sendMessage(phone, code);
            return buildResult(null, phone, response);
        } catch (Exception e) {
            log.error("发送短信验证码失败，phone: {}", PhoneUtils.mask(phone), e);
            return SmsResult.fail(null, phone, SmsErrorCode.SEND_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 发送模板短信
     *
     * @param configId   配置标识
     * @param phone      手机号
     * @param templateId 模板ID
     * @param params     模板参数
     *
     * @return 发送结果
     */
    public SmsResult sendTemplate(String configId, String phone, String templateId, Map<String, String> params) {
        try {
            SmsBlend smsBlend = getSmsBlend(configId);
            LinkedHashMap<String, String> linkedParams = new LinkedHashMap<>(params);
            SmsResponse response = smsBlend.sendMessage(phone, templateId, linkedParams);
            return buildResult(configId, phone, response);
        } catch (Exception e) {
            log.error("发送模板短信失败，configId: {}, phone: {}, templateId: {}", configId, PhoneUtils.mask(phone), templateId, e);
            return SmsResult.fail(configId, phone, SmsErrorCode.SEND_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 发送模板短信（使用默认配置）
     *
     * @param phone      手机号
     * @param templateId 模板ID
     * @param params     模板参数
     *
     * @return 发送结果
     */
    public SmsResult sendTemplate(String phone, String templateId, Map<String, String> params) {
        try {
            SmsBlend smsBlend = SmsFactory.getSmsBlend();
            LinkedHashMap<String, String> linkedParams = new LinkedHashMap<>(params);
            SmsResponse response = smsBlend.sendMessage(phone, templateId, linkedParams);
            return buildResult(null, phone, response);
        } catch (Exception e) {
            log.error("发送模板短信失败，phone: {}, templateId: {}", PhoneUtils.mask(phone), templateId, e);
            return SmsResult.fail(null, phone, SmsErrorCode.SEND_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * 批量发送短信
     *
     * @param configId   配置标识
     * @param phones     手机号列表
     * @param templateId 模板ID
     * @param params     模板参数
     *
     * @return 发送结果列表
     */
    public List<SmsResult> batchSend(String configId, List<String> phones, String templateId, Map<String, String> params) {
        return phones.stream()
                     .map(phone -> sendTemplate(configId, phone, templateId, params))
                     .toList();
    }

    /**
     * 批量发送短信（异步）
     * <p>
     * 使用 SMS4J 内置的异步批量发送功能。
     * </p>
     *
     * @param configId   配置标识
     * @param phones     手机号列表
     * @param templateId 模板ID
     * @param params     模板参数
     */
    public void batchSendAsync(String configId, List<String> phones, String templateId, Map<String, String> params) {
        try {
            SmsBlend smsBlend = getSmsBlend(configId);
            LinkedHashMap<String, String> linkedParams = new LinkedHashMap<>(params);
            smsBlend.massTexting(phones, templateId, linkedParams);
            log.info("异步批量发送短信已提交，configId: {}, 手机号数量: {}", configId, phones.size());
        } catch (Exception e) {
            log.error("异步批量发送短信失败，configId: {}", configId, e);
            throw new RuntimeException("异步批量发送短信失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取原生 SmsBlend 实例
     * <p>
     * 供高级用户直接使用 SMS4J 原生 API。
     * </p>
     *
     * @param configId 配置标识
     *
     * @return SmsBlend 实例
     */
    public SmsBlend getBlend(String configId) {
        return getSmsBlend(configId);
    }

    /**
     * 获取 SmsBlend 实例
     *
     * @param configId 配置标识
     *
     * @return SmsBlend 实例
     *
     * @throws IllegalArgumentException 如果配置不存在
     */
    private SmsBlend getSmsBlend(String configId) {
        SmsBlend smsBlend = SmsFactory.getSmsBlend(configId);
        if (smsBlend == null) {
            throw new IllegalArgumentException("未找到短信配置: " + configId);
        }
        return smsBlend;
    }

    /**
     * 构建发送结果
     *
     * @param configId 配置标识
     * @param phone    手机号
     * @param response SMS4J 响应
     *
     * @return 发送结果
     */
    private SmsResult buildResult(String configId, String phone, SmsResponse response) {
        if (response.isSuccess()) {
            return SmsResult.builder()
                            .success(true)
                            .configId(configId)
                            .phone(phone)
                            .message(SmsErrorCode.SUCCESS.getMessage())
                            .rawResponse(response.getData())
                            .build();
        } else {
            return SmsResult.builder()
                            .success(false)
                            .configId(configId)
                            .phone(phone)
                            .errorCode(SmsErrorCode.SEND_FAILED.getCode())
                            .message(SmsErrorCode.SEND_FAILED.getMessage())
                            .rawResponse(response.getData())
                            .build();
        }
    }

}
