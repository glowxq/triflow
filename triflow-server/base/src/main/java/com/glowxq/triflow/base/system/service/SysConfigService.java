package com.glowxq.triflow.base.system.service;

import com.glowxq.triflow.base.system.entity.SysConfig;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysConfigVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 系统配置服务接口
 * <p>
 * 定义系统配置业务逻辑的契约。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
public interface SysConfigService {

    /**
     * 创建配置
     *
     * @param dto 创建请求参数
     * @return 创建后的配置ID
     */
    Long create(SysConfigCreateDTO dto);

    /**
     * 更新配置
     *
     * @param dto 更新请求参数
     * @return 是否更新成功
     */
    boolean update(SysConfigUpdateDTO dto);

    /**
     * 根据ID获取配置详情
     *
     * @param id 配置ID
     * @return 配置 VO
     */
    SysConfigVO getById(Long id);

    /**
     * 根据配置键获取配置
     *
     * @param configKey 配置键
     * @return 配置实体
     */
    SysConfig getByConfigKey(String configKey);

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getValueByKey(String configKey);

    /**
     * 根据配置键获取配置值，不存在时返回默认值
     *
     * @param configKey    配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getValueByKey(String configKey, String defaultValue);

    /**
     * 根据ID删除配置
     *
     * @param id 配置ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除配置
     *
     * @param ids 配置ID列表
     * @return 是否删除成功
     */
    boolean deleteByIds(List<Long> ids);

    /**
     * 根据条件查询配置列表
     *
     * @param query 查询条件
     * @return 配置列表
     */
    List<SysConfigVO> list(SysConfigQueryDTO query);

    /**
     * 分页查询配置列表
     *
     * @param query 查询条件（包含分页参数）
     * @return 分页结果
     */
    Page<SysConfigVO> page(SysConfigQueryDTO query);

    /**
     * 根据分类获取配置列表
     *
     * @param category 分类
     * @return 配置列表
     */
    List<SysConfigVO> listByCategory(String category);

    /**
     * 获取所有启用的配置
     *
     * @return 配置列表
     */
    List<SysConfigVO> listAllEnabled();

    /**
     * 刷新配置缓存
     */
    void refreshCache();

}
