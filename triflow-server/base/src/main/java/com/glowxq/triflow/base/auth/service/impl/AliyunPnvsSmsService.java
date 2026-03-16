package com.glowxq.triflow.base.auth.service.impl;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponseBody;
import darabonba.core.client.ClientOverrideConfiguration;
import com.glowxq.common.core.util.JsonUtils;
import com.glowxq.common.sms.model.SmsResult;
import com.glowxq.triflow.base.auth.config.SmsProviderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 阿里云号码认证短信服务
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunPnvsSmsService {

    private static final String SUCCESS_CODE = "OK";

    private final SmsProviderProperties smsProviderProperties;

    public SmsResult sendVerifyCode(String phone, String templateCode, Map<String, String> templateParams) {
        SmsProviderProperties.AliyunPnvsProperties properties = smsProviderProperties.getPnvs();
        SmsResult validationResult = validate(properties, phone, templateCode);
        if (validationResult != null) {
            return validationResult;
        }
        try {
            try (AsyncClient client = createClient(properties)) {
                SendSmsVerifyCodeRequest request = buildRequest(properties, phone, templateCode, templateParams);
                SendSmsVerifyCodeResponse response = client.sendSmsVerifyCode(request).get();
                return buildResult(phone, response);
            }
        } catch (Exception e) {
            log.error("阿里云号码认证短信发送失败, phone={}, templateCode={}, error={}", phone, templateCode, e.getMessage(), e);
            return SmsResult.fail(smsProviderProperties.getProvider(), phone, "SEND_ERROR", e.getMessage());
        }
    }

    public String extractVerifyCode(Object rawResponse) {
        if (rawResponse instanceof SendSmsVerifyCodeResponseBody body) {
            if (body.getModel() != null) {
                return body.getModel().getVerifyCode();
            }
        }
        return null;
    }

    private AsyncClient createClient(SmsProviderProperties.AliyunPnvsProperties properties) {
        Credential credential = Credential.builder()
                                           .accessKeyId(properties.getAccessKeyId())
                                           .accessKeySecret(properties.getAccessKeySecret())
                                           .build();
        StaticCredentialProvider provider = StaticCredentialProvider.create(credential);
        ClientOverrideConfiguration overrideConfiguration = ClientOverrideConfiguration.create()
                                                                                      .setEndpointOverride(properties.getEndpoint());
        return AsyncClient.builder()
                          .credentialsProvider(provider)
                          .overrideConfiguration(overrideConfiguration)
                          .build();
    }

    private SendSmsVerifyCodeRequest buildRequest(
        SmsProviderProperties.AliyunPnvsProperties properties,
        String phone,
        String templateCode,
        Map<String, String> templateParams
    ) {
        SendSmsVerifyCodeRequest.Builder builder = SendSmsVerifyCodeRequest.builder()
                                                                           .phoneNumber(phone)
                                                                           .signName(properties.getSignName())
                                                                           .templateCode(templateCode)
                                                                           .templateParam(JsonUtils.toJsonString(templateParams))
                                                                           .countryCode(properties.getCountryCode())
                                                                           .validTime(properties.getValidTime())
                                                                           .interval(properties.getInterval())
                                                                           .codeLength(properties.getCodeLength())
                                                                           .codeType(properties.getCodeType())
                                                                           .duplicatePolicy(properties.getDuplicatePolicy())
                                                                           .autoRetry(properties.getAutoRetry())
                                                                           .returnVerifyCode(properties.getReturnVerifyCode());

        if (StringUtils.hasText(properties.getSchemeName())) {
            builder.schemeName(properties.getSchemeName());
        }

        return builder.build();
    }

    private SmsResult buildResult(String phone, SendSmsVerifyCodeResponse response) {
        SendSmsVerifyCodeResponseBody body = response != null ? response.getBody() : null;
        if (body != null && Boolean.TRUE.equals(body.getSuccess()) && SUCCESS_CODE.equalsIgnoreCase(body.getCode())) {
            return SmsResult.builder()
                            .success(true)
                            .configId(smsProviderProperties.getProvider())
                            .phone(phone)
                            .message(body.getMessage())
                            .rawResponse(body)
                            .build();
        }
        String errorCode = body != null ? body.getCode() : "SEND_FAILED";
        String errorMessage = body != null ? body.getMessage() : "发送失败";
        Object rawResponse = body != null ? body : null;
        return SmsResult.builder()
                        .success(false)
                        .configId(smsProviderProperties.getProvider())
                        .phone(phone)
                        .errorCode(errorCode)
                        .message(errorMessage)
                        .rawResponse(rawResponse)
                        .build();
    }

    private SmsResult validate(SmsProviderProperties.AliyunPnvsProperties properties, String phone, String templateCode) {
        if (!StringUtils.hasText(properties.getAccessKeyId()) || !StringUtils.hasText(properties.getAccessKeySecret())) {
            return SmsResult.fail(smsProviderProperties.getProvider(), phone, "CONFIG_MISSING", "阿里云 AccessKey 未配置");
        }
        if (!StringUtils.hasText(properties.getSignName())) {
            return SmsResult.fail(smsProviderProperties.getProvider(), phone, "CONFIG_MISSING", "阿里云签名未配置");
        }
        if (!StringUtils.hasText(templateCode)) {
            return SmsResult.fail(smsProviderProperties.getProvider(), phone, "CONFIG_MISSING", "短信模板未配置");
        }
        return null;
    }
}
