package com.glowxq.triflow.base.wallet.service;

import com.glowxq.triflow.base.wallet.pojo.dto.WalletSignInConfigUpdateDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletSignInQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInConfigVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInStatusVO;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletSignInVO;
import com.mybatisflex.core.paginate.Page;

/**
 * 钱包签到服务接口
 *
 * @author glowxq
 * @since 2025-02-10
 */
public interface WalletSignInService {

    WalletSignInStatusVO getStatus();

    WalletSignInVO signIn();

    Page<WalletSignInVO> page(WalletSignInQuery query);

    WalletSignInConfigVO getConfig();

    boolean updateConfig(WalletSignInConfigUpdateDTO dto);
}
