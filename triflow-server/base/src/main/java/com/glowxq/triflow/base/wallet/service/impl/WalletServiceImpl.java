package com.glowxq.triflow.base.wallet.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.triflow.base.system.entity.SysUser;
import com.glowxq.triflow.base.system.mapper.SysUserMapper;
import com.glowxq.triflow.base.wallet.entity.WalletTransaction;
import com.glowxq.triflow.base.wallet.enums.WalletAction;
import com.glowxq.triflow.base.wallet.enums.WalletType;
import com.glowxq.triflow.base.wallet.mapper.WalletTransactionMapper;
import com.glowxq.triflow.base.wallet.pojo.dto.WalletAdjustDTO;
import com.glowxq.triflow.base.wallet.pojo.query.WalletTransactionQuery;
import com.glowxq.triflow.base.wallet.pojo.vo.WalletTransactionVO;
import com.glowxq.triflow.base.wallet.service.WalletService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 钱包服务实现
 * <p>
 * 提供积分、余额的增减操作及变动记录查询。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletTransactionMapper transactionMapper;
    private final SysUserMapper userMapper;

    @Override
    public Page<WalletTransactionVO> page(WalletTransactionQuery query) {
        Page<WalletTransaction> page;

        // 如果有用户名查询条件，需要先查询匹配的用户ID列表
        if (StringUtils.isNotBlank(query.getUsername())) {
            List<SysUser> matchedUsers = userMapper.selectByKeyword(query.getUsername());
            List<Long> userIds = matchedUsers.stream().map(SysUser::getId).toList();
            page = transactionMapper.paginateByQueryAndUserIds(
                query.getPageNum(), query.getPageSize(), query, userIds);
        } else {
            QueryWrapper qw = transactionMapper.buildQueryWrapper(query);
            page = transactionMapper.paginate(query.buildPage(), qw);
        }

        List<WalletTransactionVO> records = enrichTransactionList(page.getRecords());
        return new Page<>(records, page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjust(WalletAdjustDTO dto) {
        WalletType type = WalletType.fromCode(dto.getType());
        WalletAction action = WalletAction.fromCode(dto.getAction());

        if (type == null || type != WalletType.POINTS) {
            throw new BusinessException("仅支持积分调整");
        }
        if (action == null) {
            throw new BusinessException("无效的操作类型");
        }
        if (action == WalletAction.FREEZE || action == WalletAction.UNFREEZE) {
            throw new BusinessException("该接口不支持冻结/解冻操作");
        }

        SysUser user = userMapper.selectOneById(dto.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取操作人信息
        String operatorName = getOperatorName();

        // 管理员手动调整积分，bizType 默认为 "admin_adjust"，会计入奖励积分
        String bizType = StringUtils.isNotBlank(dto.getBizType()) ? dto.getBizType() : "admin_adjust";

        if (action == WalletAction.INCOME) {
            addPoints(dto.getUserId(), dto.getAmount().longValue(), dto.getTitle(),
                    bizType, dto.getBizId(), dto.getRemark());
        } else {
            deductPoints(dto.getUserId(), dto.getAmount().longValue(), dto.getTitle(),
                    bizType, dto.getBizId(), dto.getRemark());
        }

        log.info("管理员调整积分: userId={}, action={}, amount={}, operator={}",
                dto.getUserId(), dto.getAction(), dto.getAmount(), operatorName);
    }

    /** 充值业务类型标识 */
    private static final String BIZ_TYPE_RECHARGE = "recharge";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long userId, Long amount, String title, String bizType, String bizId, String remark) {
        if (amount <= 0) {
            throw new BusinessException("积分金额必须大于0");
        }

        SysUser user = userMapper.selectOneById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Long beforePoints = user.getPoints() != null ? user.getPoints() : 0L;
        Long afterPoints = beforePoints + amount;

        // 更新用户积分
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPoints(afterPoints);
        updateUser.setUpdateTime(LocalDateTime.now());

        // 非充值来源的积分，同时更新奖励积分（用于退费计算）
        if (!BIZ_TYPE_RECHARGE.equals(bizType)) {
            Long beforeRewardPoints = user.getRewardPoints() != null ? user.getRewardPoints() : 0L;
            updateUser.setRewardPoints(beforeRewardPoints + amount);
            log.info("增加奖励积分: userId={}, amount={}, bizType={}", userId, amount, bizType);
        }

        userMapper.update(updateUser);

        // 记录变动
        saveTransaction(userId, WalletType.POINTS, WalletAction.INCOME,
                new BigDecimal(amount), new BigDecimal(beforePoints), new BigDecimal(afterPoints),
                title, bizType, bizId, remark);

        log.info("增加积分: userId={}, amount={}, before={}, after={}, bizType={}", userId, amount, beforePoints, afterPoints, bizType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductPoints(Long userId, Long amount, String title, String bizType, String bizId, String remark) {
        if (amount <= 0) {
            throw new BusinessException("积分金额必须大于0");
        }

        SysUser user = userMapper.selectOneById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Long beforePoints = user.getPoints() != null ? user.getPoints() : 0L;
        if (beforePoints < amount) {
            throw new BusinessException("积分不足");
        }

        Long afterPoints = beforePoints - amount;

        // 更新用户积分
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPoints(afterPoints);
        updateUser.setUpdateTime(LocalDateTime.now());
        userMapper.update(updateUser);

        // 记录变动（金额为负数）
        saveTransaction(userId, WalletType.POINTS, WalletAction.EXPENSE,
                new BigDecimal(-amount), new BigDecimal(beforePoints), new BigDecimal(afterPoints),
                title, bizType, bizId, remark);

        log.info("扣减积分: userId={}, amount={}, before={}, after={}", userId, amount, beforePoints, afterPoints);
    }

    /**
     * @deprecated 余额功能已废弃，请使用积分功能
     */
    @Deprecated
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBalance(Long userId, BigDecimal amount, String title, String bizType, String bizId, String remark) {
        throw new BusinessException("余额功能已废弃，请使用积分功能");
    }

    /**
     * @deprecated 余额功能已废弃，请使用积分功能
     */
    @Deprecated
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductBalance(Long userId, BigDecimal amount, String title, String bizType, String bizId, String remark) {
        throw new BusinessException("余额功能已废弃，请使用积分功能");
    }

    @Override
    public Page<WalletTransactionVO> myTransactions(WalletTransactionQuery query) {
        Long userId = LoginUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        query.setUserId(userId);
        return page(query);
    }

    // ==================== 私有方法 ====================

    /**
     * 获取当前操作人名称
     *
     * @return 操作人名称
     */
    private String getOperatorName() {
        var loginUser = LoginUtils.getLoginUser();
        if (loginUser != null && loginUser.getUserInfo() != null) {
            return loginUser.getUserInfo().getName();
        }
        return "系统";
    }

    /**
     * 补充变动记录的用户信息和类型描述
     *
     * @param records 变动记录列表
     * @return 补充后的 VO 列表
     */
    private List<WalletTransactionVO> enrichTransactionList(List<WalletTransaction> records) {
        if (CollectionUtils.isEmpty(records)) {
            return List.of();
        }

        // 获取用户信息
        List<Long> userIds = records.stream().map(WalletTransaction::getUserId).distinct().toList();
        Map<Long, SysUser> userMap = userMapper.selectListByIds(userIds)
                .stream().collect(Collectors.toMap(SysUser::getId, u -> u));

        return records.stream().map(record -> {
            WalletTransactionVO vo = MapStructUtils.convert(record, WalletTransactionVO.class);

            // 补充用户信息
            SysUser user = userMap.get(record.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
            }

            // 补充类型描述
            WalletType type = WalletType.fromCode(record.getType());
            if (type != null) {
                vo.setTypeDesc(type.getName());
            }

            WalletAction action = WalletAction.fromCode(record.getAction());
            if (action != null) {
                vo.setActionDesc(action.getName());
            }

            return vo;
        }).toList();
    }

    /**
     * 保存钱包变动记录
     *
     * @param userId       用户ID
     * @param type         交易类型
     * @param action       操作类型
     * @param amount       变动金额
     * @param beforeAmount 变动前金额
     * @param afterAmount  变动后金额
     * @param title        标题
     * @param bizType      业务类型
     * @param bizId        业务ID
     * @param remark       备注
     */
    private void saveTransaction(Long userId, WalletType type, WalletAction action,
                                  BigDecimal amount, BigDecimal beforeAmount, BigDecimal afterAmount,
                                  String title, String bizType, String bizId, String remark) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType(type.getCode());
        transaction.setAction(action.getCode());
        transaction.setAmount(amount);
        transaction.setBeforeAmount(beforeAmount);
        transaction.setAfterAmount(afterAmount);
        transaction.setTitle(title);
        transaction.setBizType(bizType);
        transaction.setBizId(bizId);
        transaction.setRemark(remark);
        // 记录操作人
        Long operatorId = LoginUtils.getUserId();
        if (operatorId != null) {
            transaction.setOperatorId(operatorId);
            transaction.setOperatorName(getOperatorName());
        }

        transactionMapper.insert(transaction);
    }
}
