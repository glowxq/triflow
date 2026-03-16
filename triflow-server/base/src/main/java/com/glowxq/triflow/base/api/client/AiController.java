package com.glowxq.triflow.base.api.client;

import com.glowxq.common.ai.service.AiService;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.annotation.ClientApi;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.triflow.base.ai.enums.AiCallStatusEnum;
import com.glowxq.triflow.base.ai.pojo.dto.AiChatDTO;
import com.glowxq.triflow.base.ai.pojo.vo.AiChatVO;
import com.glowxq.triflow.base.ai.pojo.vo.AiProviderVO;
import com.glowxq.triflow.base.ai.service.AiCallLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * AI 服务接口
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
@ClientApi
@RestController
@RequestMapping("/base/client/ai")
@RequiredArgsConstructor
@Tag(name = "AI 服务", description = "AI 聊天接口")
@OperationLog(module = ModuleEnum.AI)
public class AiController extends BaseApi {

    private final AiService aiService;
    private final AiCallLogService aiCallLogService;

    /**
     * 获取可用的 AI 提供商列表
     */
    @GetMapping("/providers")
    @Operation(summary = "获取 AI 提供商列表", description = "返回所有配置的 AI 提供商及其可用状态")
    public ApiResult<List<AiProviderVO>> getProviders() {
        Set<String> available = aiService.getAvailableProviders();
        String defaultProvider = aiService.getDefaultProvider();

        List<AiProviderVO> providers = available.stream()
                .map(code -> AiProviderVO.builder()
                        .code(code)
                        .name(code)
                        .available(true)
                        .isDefault(code.equals(defaultProvider))
                        .build())
                .toList();

        return ApiResult.success(providers);
    }

    /**
     * AI 聊天
     */
    @PostMapping("/chat")
    @Operation(summary = "AI 聊天", description = "发送消息到 AI 并获取响应")
    public ApiResult<AiChatVO> chat(@Valid @RequestBody AiChatDTO dto, HttpServletRequest httpRequest) {
        long startTime = System.currentTimeMillis();

        // 获取用户信息
        Long userId = LoginUtils.getUserId();
        String username = null;
        var loginUser = LoginUtils.getLoginUser();
        if (loginUser != null && loginUser.getUserInfo() != null) {
            username = loginUser.getUserInfo().getUsername();
        }
        String ip = getClientIp(httpRequest);

        // 确定使用的提供商
        String provider = StringUtils.isNotBlank(dto.getProvider())
                ? dto.getProvider()
                : aiService.getDefaultProvider();

        try {
            // 发送请求
            String content = aiService.chat(provider, dto.getSystemPrompt(), dto.getMessage());
            long duration = System.currentTimeMillis() - startTime;

            // 构建响应
            AiChatVO vo = AiChatVO.builder()
                    .provider(provider)
                    .content(content)
                    .build();

            // 异步保存调用记录（成功）
            aiCallLogService.saveLog(userId, username, provider, dto.getModel(),
                    dto.getSystemPrompt(), dto.getMessage(), content,
                    null, null, null,
                    duration, AiCallStatusEnum.SUCCESS.getValue(), null, ip);

            return ApiResult.success(vo);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            // 异步保存调用记录（失败）
            aiCallLogService.saveLog(userId, username, provider, dto.getModel(),
                    dto.getSystemPrompt(), dto.getMessage(), null,
                    null, null, null,
                    duration, AiCallStatusEnum.FAILED.getValue(), e.getMessage(), ip);

            throw e;
        }
    }

    /**
     * 简单聊天接口
     */
    @PostMapping("/simple-chat")
    @Operation(summary = "简单聊天", description = "发送简单消息到默认 AI 并获取文本响应")
    public ApiResult<String> simpleChat(@RequestParam String message, HttpServletRequest httpRequest) {
        long startTime = System.currentTimeMillis();

        // 获取用户信息
        Long userId = LoginUtils.getUserId();
        String username = null;
        var loginUser = LoginUtils.getLoginUser();
        if (loginUser != null && loginUser.getUserInfo() != null) {
            username = loginUser.getUserInfo().getUsername();
        }
        String ip = getClientIp(httpRequest);
        String provider = aiService.getDefaultProvider();

        try {
            String response = aiService.chat(message);
            long duration = System.currentTimeMillis() - startTime;

            // 异步保存调用记录（成功）
            aiCallLogService.saveLog(userId, username, provider, null,
                    null, message, response,
                    null, null, null,
                    duration, AiCallStatusEnum.SUCCESS.getValue(), null, ip);

            return ApiResult.success(response);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            // 异步保存调用记录（失败）
            aiCallLogService.saveLog(userId, username, provider, null,
                    null, message, null,
                    null, null, null,
                    duration, AiCallStatusEnum.FAILED.getValue(), e.getMessage(), ip);

            throw e;
        }
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个代理的情况，取第一个非 unknown 的 IP
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
