package com.glowxq.triflow.base.system.service.impl;

import com.glowxq.triflow.base.system.entity.SysUserSocial;
import com.glowxq.triflow.base.system.mapper.SysUserSocialMapper;
import com.glowxq.triflow.base.system.service.SysUserSocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户第三方账号服务实现类
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserSocialServiceImpl implements SysUserSocialService {

    private final SysUserSocialMapper userSocialMapper;

    @Override
    public SysUserSocial getBySocialTypeAndId(String socialType, String socialId) {
        return userSocialMapper.selectBySocialTypeAndId(socialType, socialId);
    }

    @Override
    public SysUserSocial getByUserIdAndType(Long userId, String socialType) {
        return userSocialMapper.selectByUserIdAndType(userId, socialType);
    }

    @Override
    public List<SysUserSocial> getByUserId(Long userId) {
        return userSocialMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(SysUserSocial social) {
        // 检查是否已存在
        SysUserSocial existing = userSocialMapper.selectBySocialTypeAndId(
            social.getSocialType(), social.getSocialId());

        if (existing != null) {
            // 更新
            social.setId(existing.getId());
            userSocialMapper.update(social);
            log.info("更新第三方账号关联, id={}, userId={}, socialType={}",
                social.getId(), social.getUserId(), social.getSocialType());
            return existing.getId();
        } else {
            // 新增
            if (social.getBindTime() == null) {
                social.setBindTime(LocalDateTime.now());
            }
            userSocialMapper.insert(social);
            log.info("创建第三方账号关联, id={}, userId={}, socialType={}",
                social.getId(), social.getUserId(), social.getSocialType());
            return social.getId();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbind(Long userId, String socialType) {
        int rows = userSocialMapper.deleteByUserIdAndType(userId, socialType);
        log.info("解除第三方账号绑定, userId={}, socialType={}, affected={}", userId, socialType, rows);
        return rows > 0;
    }

}
