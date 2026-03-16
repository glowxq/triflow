package com.glowxq.common.wechat.pay.service.impl;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.glowxq.common.wechat.pay.service.WechatPaymentNotifyService;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信支付回调处理器抽象基类
 * <p>
 * 提供支付回调的基础处理逻辑，包括日志记录和异常处理。
 * 业务方只需继承此类并实现 {@link #doPaymentNotify(WxPayNotifyV3Result)} 方法即可。
 * </p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * @Service
 * public class OrderPaymentNotifyHandler extends AbstractPaymentNotifyHandler {
 *
 *     private final OrderService orderService;
 *
 *     @Override
 *     protected void doPaymentNotify(WxPayNotifyV3Result result) {
 *         WxPayNotifyV3Result.DecryptNotifyResult decryptResult = result.getResult();
 *
 *         // 获取订单号
 *         String outTradeNo = decryptResult.getOutTradeNo();
 *
 *         // 获取支付状态
 *         String tradeState = decryptResult.getTradeState();
 *
 *         if ("SUCCESS".equals(tradeState)) {
 *             // 处理支付成功
 *             orderService.handlePaymentSuccess(outTradeNo, decryptResult.getTransactionId());
 *         }
 *     }
 * }
 * }</pre>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Slf4j
public abstract class AbstractPaymentNotifyHandler implements WechatPaymentNotifyService {

    /**
     * 处理支付回调通知
     * <p>
     * 模板方法，提供统一的日志记录和异常处理。
     * 具体的业务逻辑由子类实现。
     * </p>
     *
     * @param wxPayNotifyV3Result 微信支付 V3 回调结果
     */
    @Override
    public void paymentNotify(WxPayNotifyV3Result wxPayNotifyV3Result) {
        WxPayNotifyV3Result.DecryptNotifyResult decryptResult = wxPayNotifyV3Result.getResult();

        log.info("收到微信支付回调通知，订单号: {}, 交易状态: {}",
                decryptResult.getOutTradeNo(),
                decryptResult.getTradeState());

        try {
            doPaymentNotify(wxPayNotifyV3Result);
            log.info("微信支付回调处理成功，订单号: {}", decryptResult.getOutTradeNo());
        } catch (Exception e) {
            log.error("微信支付回调处理失败，订单号: {}", decryptResult.getOutTradeNo(), e);
            throw new RuntimeException("微信支付回调处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 执行具体的支付回调业务逻辑
     * <p>
     * 子类需要实现此方法来处理具体的业务逻辑。
     * </p>
     *
     * @param wxPayNotifyV3Result 微信支付 V3 回调结果
     */
    protected abstract void doPaymentNotify(WxPayNotifyV3Result wxPayNotifyV3Result);

}
