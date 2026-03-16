package com.glowxq.triflow.base.wallet.service;

import com.glowxq.triflow.base.wallet.pojo.dto.WalletAdjustDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletTransactionQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletTransactionVO;
import com.mybatisflex.core.paginate.Page;

import java.math.BigDecimal;

/**
 * 钱包服务接口
 *
 * @author glowxq
 * @since 2025-01-28
 */
public interface WalletService {

    /**
     * 分页查询钱包变动记录
     *
     * @param query 查询参数
     * @return 分页结果
     */
    Page<WalletTransactionVO> page(WalletTransactionQuery query);

    /**
     * 调整用户钱包（管理员操作）
     *
     * @param dto 调整参数
     */
    void adjust(WalletAdjustDTO dto);

    /**
     * 增加积分
     *
     * @param userId  用户ID
     * @param amount  金额
     * @param title   标题
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @param remark  备注
     */
    void addPoints(Long userId, Long amount, String title, String bizType, String bizId, String remark);

    /**
     * 扣减积分
     *
     * @param userId  用户ID
     * @param amount  金额
     * @param title   标题
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @param remark  备注
     */
    void deductPoints(Long userId, Long amount, String title, String bizType, String bizId, String remark);

    /**
     * 增加余额
     *
     * @param userId  用户ID
     * @param amount  金额
     * @param title   标题
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @param remark  备注
     */
    void addBalance(Long userId, BigDecimal amount, String title, String bizType, String bizId, String remark);

    /**
     * 扣减余额
     *
     * @param userId  用户ID
     * @param amount  金额
     * @param title   标题
     * @param bizType 业务类型
     * @param bizId   业务ID
     * @param remark  备注
     */
    void deductBalance(Long userId, BigDecimal amount, String title, String bizType, String bizId, String remark);

    /**
     * 获取当前用户的钱包变动记录
     *
     * @param query 查询参数
     * @return 分页结果
     */
    Page<WalletTransactionVO> myTransactions(WalletTransactionQuery query);
}
