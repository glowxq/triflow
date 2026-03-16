package com.glowxq.triflow.base.system.mapper;

import com.glowxq.triflow.base.system.entity.SysUserSocial;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户第三方账号关联数据访问层
 * <p>
 * 提供用户第三方账号关联的数据库操作方法。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Mapper
public interface SysUserSocialMapper extends BaseMapper<SysUserSocial> {

    /**
     * 根据第三方平台类型和平台用户ID查询
     *
     * @param socialType 第三方平台类型
     * @param socialId   第三方平台用户ID
     * @return 关联信息
     */
    default SysUserSocial selectBySocialTypeAndId(String socialType, String socialId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUserSocial.class)
                                      .eq(SysUserSocial::getSocialType, socialType)
                                      .eq(SysUserSocial::getSocialId, socialId);
        return selectOneByQuery(qw);
    }

    /**
     * 根据用户ID查询所有第三方账号关联
     *
     * @param userId 用户ID
     * @return 关联列表
     */
    default List<SysUserSocial> selectByUserId(Long userId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUserSocial.class)
                                      .eq(SysUserSocial::getUserId, userId);
        return selectListByQuery(qw);
    }

    /**
     * 根据UnionId查询
     *
     * @param unionId 统一ID
     * @return 关联信息
     */
    default SysUserSocial selectByUnionId(String unionId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUserSocial.class)
                                      .eq(SysUserSocial::getUnionId, unionId);
        return selectOneByQuery(qw);
    }

    /**
     * 根据用户ID和第三方平台类型查询
     *
     * @param userId     用户ID
     * @param socialType 第三方平台类型
     * @return 关联信息
     */
    default SysUserSocial selectByUserIdAndType(Long userId, String socialType) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUserSocial.class)
                                      .eq(SysUserSocial::getUserId, userId)
                                      .eq(SysUserSocial::getSocialType, socialType);
        return selectOneByQuery(qw);
    }

    /**
     * 删除用户的某个第三方账号关联
     *
     * @param userId     用户ID
     * @param socialType 第三方平台类型
     * @return 删除数量
     */
    default int deleteByUserIdAndType(Long userId, String socialType) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(SysUserSocial.class)
                                      .eq(SysUserSocial::getUserId, userId)
                                      .eq(SysUserSocial::getSocialType, socialType);
        return deleteByQuery(qw);
    }

}
