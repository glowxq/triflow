package com.glowxq.triflow.base.api.anon;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.triflow.base.auth.enums.SmsCodeTypeEnum;
import com.glowxq.triflow.base.auth.pojo.dto.SendSmsCodeDTO;
import com.glowxq.triflow.base.auth.service.SmsCodeService;
import com.glowxq.triflow.base.system.enums.SwitchKeyEnum;
import com.glowxq.triflow.base.system.service.SysSwitchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AnonApi;

/**
 * 短信验证码控制器
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Tag(name = "短信验证码", description = "短信验证码发送接口（公开接口，无需登录）")
@AnonApi
@RestController
@RequestMapping("/base/public/sms")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Client)
public class SmsController extends BaseApi {

    private final SmsCodeService smsCodeService;
    private final SysSwitchService switchService;

    /**
     * 发送短信验证码
     */
    @Operation(summary = "发送短信验证码", description = "发送登录/注册/重置密码验证码")
    @PostMapping("/send")
    public ApiResult<Void> sendCode(@Valid @RequestBody SendSmsCodeDTO dto) {
        if (SmsCodeTypeEnum.LOGIN.equals(dto.getType()) && !switchService.isEnabled(SwitchKeyEnum.USER_LOGIN_PHONE)) {
            throw new BusinessException("手机号登录已关闭");
        }
        smsCodeService.sendCode(dto.getPhone(), dto.getType().getCode());
        return ApiResult.success(null);
    }

}
