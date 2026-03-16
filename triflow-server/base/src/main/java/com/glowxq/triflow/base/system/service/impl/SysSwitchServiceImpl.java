package com.glowxq.triflow.base.system.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.triflow.base.system.entity.SysSwitch;
import com.glowxq.triflow.base.system.entity.SysSwitchLog;
import com.glowxq.triflow.base.system.mapper.SysSwitchLogMapper;
import com.glowxq.triflow.base.system.mapper.SysSwitchMapper;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysSwitchLogVO;
import com.glowxq.triflow.base.system.pojo.vo.SysSwitchVO;
import com.glowxq.triflow.base.system.service.SysSwitchService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统开关服务实现类
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysSwitchServiceImpl implements SysSwitchService {

    private final SysSwitchMapper switchMapper;
    private final SysSwitchLogMapper switchLogMapper;
    private final ObjectMapper objectMapper;

    /** 开关缓存 */
    private final Map<String, SysSwitch> switchCache = new ConcurrentHashMap<>();

    /** 随机数生成器（用于灰度百分比判断） */
    private final Random random = new Random();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysSwitchCreateDTO dto) {
        // 检查开关键是否存在
        if (switchMapper.existsBySwitchKey(dto.getSwitchKey(), null)) {
            throw new BusinessException("开关键已存在");
        }

        // DTO → Entity 转换
        SysSwitch switchEntity = MapStructUtils.convert(dto, SysSwitch.class);

        // 设置默认值
        if (switchEntity.getSwitchValue() == null) {
            switchEntity.setSwitchValue(1);
        }
        if (StringUtils.isBlank(switchEntity.getSwitchType())) {
            switchEntity.setSwitchType("feature");
        }
        if (StringUtils.isBlank(switchEntity.getScope())) {
            switchEntity.setScope("global");
        }
        if (StringUtils.isBlank(switchEntity.getStrategy())) {
            switchEntity.setStrategy("all");
        }
        if (switchEntity.getPercentage() == null) {
            switchEntity.setPercentage(100);
        }
        if (switchEntity.getSort() == null) {
            switchEntity.setSort(0);
        }

        // 持久化
        switchMapper.insert(switchEntity);

        // 更新缓存
        switchCache.put(switchEntity.getSwitchKey(), switchEntity);

        log.info("创建开关成功, id={}, key={}", switchEntity.getId(), switchEntity.getSwitchKey());
        return switchEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysSwitchUpdateDTO dto) {
        SysSwitch existingSwitch = switchMapper.selectOneById(dto.getId());
        if (existingSwitch == null) {
            log.warn("更新开关失败, 开关不存在, id={}", dto.getId());
            return false;
        }

        // 检查开关键唯一性
        if (StringUtils.isNotBlank(dto.getSwitchKey())
            && switchMapper.existsBySwitchKey(dto.getSwitchKey(), dto.getId())) {
            throw new BusinessException("开关键已存在");
        }

        // 记录变更日志
        if (dto.getSwitchValue() != null && !dto.getSwitchValue().equals(existingSwitch.getSwitchValue())) {
            recordSwitchLog(existingSwitch, dto.getSwitchValue(), "manual", dto.getChangeReason());
        }

        // DTO → Entity 转换
        SysSwitch switchEntity = MapStructUtils.convert(dto, SysSwitch.class);

        // 执行更新
        int rows = switchMapper.update(switchEntity);

        // 更新缓存
        SysSwitch updated = switchMapper.selectOneById(dto.getId());
        if (updated != null) {
            switchCache.put(updated.getSwitchKey(), updated);
        }

        log.info("更新开关, id={}, affected={}", dto.getId(), rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggle(Long id, Integer switchValue, String changeReason) {
        SysSwitch existingSwitch = switchMapper.selectOneById(id);
        if (existingSwitch == null) {
            return false;
        }

        // 记录变更日志
        recordSwitchLog(existingSwitch, switchValue, "manual", changeReason);

        // 更新开关状态
        SysSwitch update = new SysSwitch();
        update.setId(id);
        update.setSwitchValue(switchValue);
        int rows = switchMapper.update(update);

        // 更新缓存
        existingSwitch.setSwitchValue(switchValue);
        switchCache.put(existingSwitch.getSwitchKey(), existingSwitch);

        log.info("切换开关状态, id={}, key={}, newValue={}", id, existingSwitch.getSwitchKey(), switchValue);
        return rows > 0;
    }

    /**
     * 记录开关变更日志
     */
    private void recordSwitchLog(SysSwitch oldSwitch, Integer newValue, String changeType, String changeReason) {
        SysSwitchLog log = new SysSwitchLog();
        log.setSwitchId(oldSwitch.getId());
        log.setSwitchKey(oldSwitch.getSwitchKey());
        log.setOldValue(oldSwitch.getSwitchValue());
        log.setNewValue(newValue);
        log.setChangeType(changeType);
        log.setChangeReason(changeReason);
        log.setOperateTime(LocalDateTime.now());

        // 获取当前登录用户信息
        try {
            Long userId = LoginUtils.getUserId();
            String username = LoginUtils.getLoginUser().getUserInfo().getUsername();
            log.setOperatorId(userId);
            log.setOperatorName(username);
        } catch (Exception e) {
            // 非登录场景（如定时任务）
            log.setOperatorId(0L);
            log.setOperatorName("system");
        }

        switchLogMapper.insert(log);
    }

    @Override
    public SysSwitchVO getById(Long id) {
        SysSwitch switchEntity = switchMapper.selectOneById(id);
        if (switchEntity == null) {
            return null;
        }
        return MapStructUtils.convert(switchEntity, SysSwitchVO.class);
    }

    @Override
    public SysSwitch getBySwitchKey(String switchKey) {
        // 先从缓存获取
        SysSwitch cached = switchCache.get(switchKey);
        if (cached != null) {
            return cached;
        }

        // 缓存未命中，从数据库获取
        SysSwitch switchEntity = switchMapper.selectBySwitchKey(switchKey);
        if (switchEntity != null) {
            switchCache.put(switchKey, switchEntity);
        }
        return switchEntity;
    }

    @Override
    public boolean isEnabled(String switchKey) {
        return isEnabled(switchKey, null);
    }

    @Override
    public boolean isEnabled(String switchKey, Long userId) {
        SysSwitch switchEntity = getBySwitchKey(switchKey);
        if (switchEntity == null || switchEntity.getSwitchValue() != 1) {
            return false;
        }

        // 检查生效策略
        String strategy = switchEntity.getStrategy();
        if ("all".equals(strategy)) {
            return true;
        }

        // 定时策略
        if ("schedule".equals(strategy)) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = switchEntity.getStartTime();
            LocalDateTime endTime = switchEntity.getEndTime();
            if (startTime != null && now.isBefore(startTime)) {
                return false;
            }
            if (endTime != null && now.isAfter(endTime)) {
                return false;
            }
            return true;
        }

        // 需要用户ID的策略
        if (userId == null) {
            return false;
        }

        // 白名单策略
        if ("whitelist".equals(strategy)) {
            String whitelist = switchEntity.getWhitelist();
            if (StringUtils.isBlank(whitelist)) {
                return false;
            }
            try {
                List<Long> whitelistIds = objectMapper.readValue(whitelist, new TypeReference<>() {});
                return whitelistIds.contains(userId);
            } catch (Exception e) {
                log.error("解析白名单配置失败, switchKey={}", switchKey, e);
                return false;
            }
        }

        // 百分比策略
        if ("percentage".equals(strategy)) {
            Integer percentage = switchEntity.getPercentage();
            if (percentage == null || percentage <= 0) {
                return false;
            }
            if (percentage >= 100) {
                return true;
            }
            // 基于用户ID的稳定哈希
            int hash = Math.abs(userId.hashCode()) % 100;
            return hash < percentage;
        }

        return false;
    }

    @Override
    public Map<String, Boolean> batchGetStatus(List<String> switchKeys) {
        Map<String, Boolean> result = new java.util.HashMap<>();
        if (CollectionUtils.isEmpty(switchKeys)) {
            return result;
        }
        for (String key : switchKeys) {
            result.put(key, isEnabled(key));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        SysSwitch switchEntity = switchMapper.selectOneById(id);
        if (switchEntity == null) {
            return false;
        }

        // 删除开关日志
        switchLogMapper.deleteBySwitchId(id);

        // 删除开关
        int rows = switchMapper.deleteById(id);

        // 移除缓存
        switchCache.remove(switchEntity.getSwitchKey());

        log.info("删除开关, id={}, key={}, affected={}", id, switchEntity.getSwitchKey(), rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }

        for (Long id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public List<SysSwitchVO> list(SysSwitchQueryDTO query) {
        List<SysSwitch> switches = switchMapper.selectByQuery(query);
        return MapStructUtils.convert(switches, SysSwitchVO.class);
    }

    @Override
    public Page<SysSwitchVO> page(SysSwitchQueryDTO query) {
        QueryWrapper qw = switchMapper.buildQueryWrapper(query);
        Page<SysSwitchVO> page = query.buildPage();
        return switchMapper.paginateAs(page, qw, SysSwitchVO.class);
    }

    @Override
    public List<SysSwitchVO> listByCategory(String category) {
        List<SysSwitch> switches = switchMapper.selectByCategory(category);
        return MapStructUtils.convert(switches, SysSwitchVO.class);
    }

    @Override
    public List<SysSwitchVO> listBySwitchType(String switchType) {
        List<SysSwitch> switches = switchMapper.selectBySwitchType(switchType);
        return MapStructUtils.convert(switches, SysSwitchVO.class);
    }

    @Override
    public List<SysSwitchVO> listAllEnabled() {
        List<SysSwitch> switches = switchMapper.selectAllEnabled();
        return MapStructUtils.convert(switches, SysSwitchVO.class);
    }

    @Override
    public List<SysSwitchLogVO> getLogsBySwitchId(Long switchId) {
        List<SysSwitchLog> logs = switchLogMapper.selectBySwitchId(switchId);
        return MapStructUtils.convert(logs, SysSwitchLogVO.class);
    }

    @Override
    public Page<SysSwitchLogVO> getLogPage(Long switchId, int pageNum, int pageSize) {
        Page<SysSwitchLog> logPage = switchLogMapper.paginateBySwitchId(pageNum, pageSize, switchId);

        Page<SysSwitchLogVO> voPage = new Page<>();
        voPage.setPageNumber(logPage.getPageNumber());
        voPage.setPageSize(logPage.getPageSize());
        voPage.setTotalPage(logPage.getTotalPage());
        voPage.setTotalRow(logPage.getTotalRow());
        voPage.setRecords(MapStructUtils.convert(logPage.getRecords(), SysSwitchLogVO.class));

        return voPage;
    }

    @Override
    public void refreshCache() {
        switchCache.clear();
        List<SysSwitch> switches = switchMapper.selectAll();
        for (SysSwitch switchEntity : switches) {
            switchCache.put(switchEntity.getSwitchKey(), switchEntity);
        }
        log.info("刷新开关缓存完成, 共加载 {} 个开关", switches.size());
    }

}
