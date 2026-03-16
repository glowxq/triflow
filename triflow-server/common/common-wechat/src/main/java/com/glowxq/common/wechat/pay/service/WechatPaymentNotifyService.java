package com.glowxq.common.wechat.pay.service;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;

/**
 * 微信支付回调处理服务接口
 * <p>
 * 业务方需要实现此接口来处理微信支付的回调通知。
 * </p>
 *
 * <h3>使用说明</h3>
 * <p>
 * 1. 实现此接口或继承 {@link com.glowxq.common.wechat.pay.service.impl.AbstractPaymentNotifyHandler}
 * </p>
 * <p>
 * 2. 在实现类中添加 {@code @Service} 注解
 * </p>
 *
 * <h3>示例</h3>
 * <pre>{@code
 * @Service
 * public class OrderPaymentNotifyHandler extends AbstractPaymentNotifyHandler {
 *
 *     @Override
 *     protected void doPaymentNotify(WxPayNotifyV3Result result) {
 *         String outTradeNo = result.getResult().getOutTradeNo();
 *         // 处理订单支付成功逻辑
 *         orderService.handlePaymentSuccess(outTradeNo);
 *     }
 * }
 * }</pre>
 *
 * @author glowxq
 * @since 2025/01/21
 */
public interface WechatPaymentNotifyService {

    /**
     * 处理支付结果通知
     * <p>
     * 当微信支付成功后，微信服务器会调用此方法通知业务系统。
     * </p>
     *
     * @param wxPayNotifyV3Result 微信支付 V3 回调结果
     */
    void paymentNotify(WxPayNotifyV3Result wxPayNotifyV3Result);
}
