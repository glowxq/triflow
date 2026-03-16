package com.glowxq.triflow.base.wallet.service;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeCreateDTO;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeOrderUpdateDTO;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletRechargeRefundDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletRechargeOrderQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargeOrderVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletRechargePayVO;
import com.mybatisflex.core.paginate.Page;

/**
 * 钱包充值订单服务接口
 *
 * @author glowxq
 * @since 2025-02-10
 */
public interface WalletRechargeOrderService {

    WalletRechargePayVO createOrder(WalletRechargeCreateDTO dto);

    void handlePayNotify(WxPayNotifyV3Result result);

    Page<WalletRechargeOrderVO> page(WalletRechargeOrderQuery query);

    Page<WalletRechargeOrderVO> myOrders(WalletRechargeOrderQuery query);

    void refund(WalletRechargeRefundDTO dto);

    boolean update(WalletRechargeOrderUpdateDTO dto);
}
