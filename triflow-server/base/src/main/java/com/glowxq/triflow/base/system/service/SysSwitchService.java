package com.glowxq.triflow.base.system.service;

import com.glowxq.triflow.base.system.entity.SysSwitch;
import com.glowxq.triflow.base.system.enums.SwitchKeyEnum;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysSwitchLogVO;
import com.glowxq.triflow.base.system.pojo.vo.SysSwitchVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;
import java.util.Map;

/**
 * 系统开关服务接口
 * <p>
 * 定义系统开关业务逻辑的契约。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
public interface SysSwitchService {

    /**
     * 创建开关
     *
     * @param dto 创建请求参数
     * @return 创建后的开关ID
     */
    Long create(SysSwitchCreateDTO dto);

    /**
     * 更新开关
     *
     * @param dto 更新请求参数
     * @return 是否更新成功
     */
    boolean update(SysSwitchUpdateDTO dto);

    /**
     * 切换开关状态
     *
     * @param id           开关ID
     * @param switchValue  新状态
     * @param changeReason 变更原因
     * @return 是否切换成功
     */
    boolean toggle(Long id, Integer switchValue, String changeReason);

    /**
     * 根据ID获取开关详情
     *
     * @param id 开关ID
     * @return 开关 VO
     */
    SysSwitchVO getById(Long id);

    /**
     * 根据开关键获取开关
     *
     * @param switchKey 开关键
     * @return 开关实体
     */
    SysSwitch getBySwitchKey(String switchKey);

    /**
     * 检查开关是否开启
     *
     * @param switchKey 开关键
     * @return true 表示开启
     */
    boolean isEnabled(String switchKey);

    /**
     * 检查开关是否开启（支持灰度策略）
     *
     * @param switchKey 开关键
     * @param userId    用户ID（用于白名单/百分比判断）
     * @return true 表示开启
     */
    boolean isEnabled(String switchKey, Long userId);

    /**
     * 检查开关是否开启（使用枚举）
     *
     * @param switchKeyEnum 开关键枚举
     * @return true 表示开启
     */
    default boolean isEnabled(SwitchKeyEnum switchKeyEnum) {
        return isEnabled(switchKeyEnum.getKey());
    }

    /**
     * 检查开关是否开启（使用枚举，支持灰度策略）
     *
     * @param switchKeyEnum 开关键枚举
     * @param userId        用户ID
     * @return true 表示开启
     */
    default boolean isEnabled(SwitchKeyEnum switchKeyEnum, Long userId) {
        return isEnabled(switchKeyEnum.getKey(), userId);
    }

    /**
     * 批量查询开关状态
     *
     * @param switchKeys 开关键列表
     * @return Map<开关键, 是否开启>
     */
    Map<String, Boolean> batchGetStatus(List<String> switchKeys);

    /**
     * 根据ID删除开关
     *
     * @param id 开关ID
     * @return 是否删除成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除开关
     *
     * @param ids 开关ID列表
     * @return 是否删除成功
     */
    boolean deleteByIds(List<Long> ids);

    /**
     * 根据条件查询开关列表
     *
     * @param query 查询条件
     * @return 开关列表
     */
    List<SysSwitchVO> list(SysSwitchQueryDTO query);

    /**
     * 分页查询开关列表
     *
     * @param query 查询条件（包含分页参数）
     * @return 分页结果
     */
    Page<SysSwitchVO> page(SysSwitchQueryDTO query);

    /**
     * 根据分类获取开关列表
     *
     * @param category 分类
     * @return 开关列表
     */
    List<SysSwitchVO> listByCategory(String category);

    /**
     * 根据开关类型获取开关列表
     *
     * @param switchType 开关类型
     * @return 开关列表
     */
    List<SysSwitchVO> listBySwitchType(String switchType);

    /**
     * 获取所有开启的开关
     *
     * @return 开关列表
     */
    List<SysSwitchVO> listAllEnabled();

    /**
     * 获取开关操作日志
     *
     * @param switchId 开关ID
     * @return 日志列表
     */
    List<SysSwitchLogVO> getLogsBySwitchId(Long switchId);

    /**
     * 分页获取开关操作日志
     *
     * @param switchId 开关ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<SysSwitchLogVO> getLogPage(Long switchId, int pageNum, int pageSize);

    /**
     * 刷新开关缓存
     */
    void refreshCache();

}
