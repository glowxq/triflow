package com.glowxq.triflow.base.wallet.mapper;

import com.glowxq.triflow.base.wallet.entity.WalletSignIn;
import com.glowxq.triflow.base.wallet.pojo.query.WalletSignInQuery;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 钱包签到记录 Mapper
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Mapper
public interface WalletSignInMapper extends BaseMapper<WalletSignIn> {

    default WalletSignIn selectByUserIdAndDate(Long userId, LocalDate signDate) {
        QueryWrapper wrapper = QueryWrapper.create()
                                           .from(WalletSignIn.class)
                                           .eq(WalletSignIn::getUserId, userId)
                                           .eq(WalletSignIn::getSignDate, signDate);
        return selectOneByQuery(wrapper);
    }

    default WalletSignIn selectLatestByUserId(Long userId) {
        QueryWrapper wrapper = QueryWrapper.create()
                                           .from(WalletSignIn.class)
                                           .eq(WalletSignIn::getUserId, userId)
                                           .orderBy(WalletSignIn::getSignDate, false)
                                           .limit(1);
        return selectOneByQuery(wrapper);
    }

    default Page<WalletSignIn> paginateByQuery(int pageNum, int pageSize, WalletSignInQuery query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    default Page<WalletSignIn> paginateByQueryAndUserIds(int pageNum, int pageSize,
                                                         WalletSignInQuery query,
                                                         List<Long> userIds) {
        QueryWrapper wrapper = buildQueryWrapper(query);
        if (CollectionUtils.isNotEmpty(userIds)) {
            wrapper.in(WalletSignIn::getUserId, userIds);
        } else if (StringUtils.isNotBlank(query.getUsername())) {
            wrapper.eq(WalletSignIn::getUserId, -1L);
        }
        return paginate(pageNum, pageSize, wrapper);
    }

    default QueryWrapper buildQueryWrapper(WalletSignInQuery query) {
        return QueryWrapper.create()
                           .from(WalletSignIn.class)
                           .eq(WalletSignIn::getUserId, query.getUserId(), query.getUserId() != null)
                           .ge(WalletSignIn::getSignDate, query.getStartDate(), query.getStartDate() != null)
                           .le(WalletSignIn::getSignDate, query.getEndDate(), query.getEndDate() != null)
                           .orderBy(WalletSignIn::getSignDate, false)
                           .orderBy(WalletSignIn::getId, false);
    }
}
