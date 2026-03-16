package com.glowxq.triflow.base.auth.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.triflow.base.auth.config.FeishuProperties;
import com.glowxq.triflow.base.auth.pojo.dto.FeishuUserDTO;
import com.glowxq.triflow.base.auth.service.FeishuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 飞书服务实现类
 * <p>
 * 实现飞书 OAuth 登录流程，包括获取授权 URL、获取用户信息等。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuServiceImpl implements FeishuService {

    private final FeishuProperties feishuProperties;
    private final ObjectMapper objectMapper;

    /** 应用访问令牌缓存（简化实现，生产环境建议使用 Redis） */
    private volatile String cachedAppAccessToken;
    private volatile long tokenExpireTime = 0;

    @Override
    public String getAuthUrl(String redirectUri, String state) {
        String appId = feishuProperties.getAppId();
        if (StringUtils.isBlank(appId)) {
            throw new BusinessException("飞书应用 App ID 未配置");
        }

        // 使用配置的 redirectUri，如果参数为空
        String actualRedirectUri = StringUtils.isNotBlank(redirectUri) ? redirectUri : feishuProperties.getRedirectUri();
        if (StringUtils.isBlank(actualRedirectUri)) {
            throw new BusinessException("飞书回调地址未配置");
        }

        // 构建授权 URL
        return String.format("%s?app_id=%s&redirect_uri=%s&state=%s",
                feishuProperties.getOauthBaseUrl(),
                appId,
                URLEncoder.encode(actualRedirectUri, StandardCharsets.UTF_8),
                StringUtils.isNotBlank(state) ? URLEncoder.encode(state, StandardCharsets.UTF_8) : "");
    }

    @Override
    public FeishuUserDTO getUserInfo(String code) {
        if (StringUtils.isBlank(code)) {
            throw new BusinessException("飞书授权码不能为空");
        }

        // 1. 获取应用访问令牌
        String appAccessToken = getAppAccessToken();

        // 2. 获取用户访问令牌
        String userAccessToken = getUserAccessToken(appAccessToken, code);

        // 3. 获取用户信息
        return fetchUserInfo(userAccessToken);
    }

    @Override
    public String getAppAccessToken() {
        // 检查缓存是否有效
        if (cachedAppAccessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return cachedAppAccessToken;
        }

        synchronized (this) {
            // 双重检查
            if (cachedAppAccessToken != null && System.currentTimeMillis() < tokenExpireTime) {
                return cachedAppAccessToken;
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("app_id", feishuProperties.getAppId());
            body.put("app_secret", feishuProperties.getAppSecret());

            try {
                HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(
                        feishuProperties.getAppTokenUrl(),
                        request,
                        String.class
                );

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new BusinessException("获取飞书应用访问令牌失败: " + response.getStatusCode());
                }

                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                int respCode = jsonNode.path("code").asInt(-1);
                if (respCode != 0) {
                    String msg = jsonNode.path("msg").asText("未知错误");
                    throw new BusinessException("获取飞书应用访问令牌失败: " + msg);
                }

                String token = jsonNode.path("app_access_token").asText();
                int expire = jsonNode.path("expire").asInt(7200);

                // 缓存令牌，提前 5 分钟过期
                cachedAppAccessToken = token;
                tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;

                log.info("获取飞书应用访问令牌成功, 过期时间: {}秒", expire);
                return token;

            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("获取飞书应用访问令牌异常", e);
                throw new BusinessException("获取飞书应用访问令牌失败: " + e.getMessage());
            }
        }
    }

    /**
     * 获取用户访问令牌
     *
     * @param appAccessToken 应用访问令牌
     * @param code           授权码
     * @return 用户访问令牌
     */
    private String getUserAccessToken(String appAccessToken, String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(appAccessToken);

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);

        try {
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    feishuProperties.getTokenUrl(),
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException("获取飞书用户访问令牌失败: " + response.getStatusCode());
            }

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            int respCode = jsonNode.path("code").asInt(-1);
            if (respCode != 0) {
                String msg = jsonNode.path("message").asText("未知错误");
                throw new BusinessException("获取飞书用户访问令牌失败: " + msg);
            }

            JsonNode data = jsonNode.path("data");
            return data.path("access_token").asText();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取飞书用户访问令牌异常", e);
            throw new BusinessException("获取飞书用户访问令牌失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     *
     * @param userAccessToken 用户访问令牌
     * @return 用户信息
     */
    private FeishuUserDTO fetchUserInfo(String userAccessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userAccessToken);

        try {
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    feishuProperties.getUserInfoUrl(),
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException("获取飞书用户信息失败: " + response.getStatusCode());
            }

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            int respCode = jsonNode.path("code").asInt(-1);
            if (respCode != 0) {
                String msg = jsonNode.path("message").asText("未知错误");
                throw new BusinessException("获取飞书用户信息失败: " + msg);
            }

            JsonNode data = jsonNode.path("data");
            FeishuUserDTO userDTO = new FeishuUserDTO();
            userDTO.setOpenId(data.path("open_id").asText(null));
            userDTO.setUnionId(data.path("union_id").asText(null));
            userDTO.setName(data.path("name").asText(null));
            userDTO.setEnName(data.path("en_name").asText(null));
            userDTO.setAvatarUrl(data.path("avatar_url").asText(null));
            userDTO.setAvatarThumb(data.path("avatar_thumb").asText(null));
            userDTO.setAvatarMiddle(data.path("avatar_middle").asText(null));
            userDTO.setAvatarBig(data.path("avatar_big").asText(null));
            userDTO.setEmail(data.path("email").asText(null));
            userDTO.setEnterpriseEmail(data.path("enterprise_email").asText(null));
            userDTO.setMobile(data.path("mobile").asText(null));
            userDTO.setTenantKey(data.path("tenant_key").asText(null));
            userDTO.setAccessToken(userAccessToken);

            log.info("获取飞书用户信息成功, openId={}, name={}", userDTO.getOpenId(), userDTO.getName());
            return userDTO;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取飞书用户信息异常", e);
            throw new BusinessException("获取飞书用户信息失败: " + e.getMessage());
        }
    }

}
