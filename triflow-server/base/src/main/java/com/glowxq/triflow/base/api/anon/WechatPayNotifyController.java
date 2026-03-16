package com.glowxq.triflow.base.api.anon;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.glowxq.common.core.common.api.BaseApi;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.glowxq.common.wechat.pay.pojo.WechatNotifyResult;
import com.glowxq.triflow.base.wallet.service.WalletRechargeOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AnonApi;

/**
 * 微信支付回调
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Slf4j
@Tag(name = "微信支付通知", description = "微信支付回调通知")
@AnonApi
@RestController
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Wallet)
public class WechatPayNotifyController extends BaseApi {

    private final WxPayService wxPayService;
    private final WalletRechargeOrderService rechargeOrderService;

    @PostMapping("/notify/wechat/payment")
    public WechatNotifyResult paymentNotify(@RequestBody String body, HttpServletRequest request) {
        try {
            SignatureHeader header = SignatureHeader.builder()
                                                    .timeStamp(request.getHeader("Wechatpay-Timestamp"))
                                                    .nonce(request.getHeader("Wechatpay-Nonce"))
                                                    .signature(request.getHeader("Wechatpay-Signature"))
                                                    .serial(request.getHeader("Wechatpay-Serial"))
                                                    .build();
            WxPayNotifyV3Result result = wxPayService.parseOrderNotifyV3Result(body, header);
            rechargeOrderService.handlePayNotify(result);
            return new WechatNotifyResult("SUCCESS", "OK");
        } catch (WxPayException e) {
            log.error("微信支付回调解析失败: {}", e.getMessage(), e);
            return new WechatNotifyResult("FAIL", "parse error");
        } catch (Exception e) {
            log.error("微信支付回调处理失败: {}", e.getMessage(), e);
            return new WechatNotifyResult("FAIL", "process error");
        }
    }
}
