package com.glowxq.triflow.base.system.service.impl;

import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.LogOperation;
import com.glowxq.triflow.base.system.mapper.LogOperationMapper;
import com.glowxq.triflow.base.system.pojo.dto.LogOperationQueryDTO;
import com.glowxq.triflow.base.system.pojo.vo.LogOperationVO;
import com.glowxq.triflow.base.system.service.LogOperationService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作日志服务实现类
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogOperationServiceImpl implements LogOperationService {

    private final LogOperationMapper logOperationMapper;

    @Override
    public Page<LogOperationVO> page(LogOperationQueryDTO query) {
        QueryWrapper qw = logOperationMapper.buildQueryWrapper(query);
        Page<LogOperationVO> page = query.buildPage();
        return logOperationMapper.paginateAs(page, qw, LogOperationVO.class);
    }

    @Override
    public LogOperationVO getById(Long id) {
        LogOperation entity = logOperationMapper.selectOneById(id);
        if (entity == null) {
            return null;
        }
        return MapStructUtils.convert(entity, LogOperationVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        int rows = logOperationMapper.deleteBatchByIds(ids);
        log.info("批量删除操作日志, ids={}, affected={}", ids, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long clear() {
        long rows = logOperationMapper.deleteAll();
        log.info("清空操作日志, affected={}", rows);
        return rows;
    }

    @Override
    public List<LogOperationVO> list(LogOperationQueryDTO query) {
        List<LogOperation> list = logOperationMapper.selectByQuery(query);
        return MapStructUtils.convert(list, LogOperationVO.class);
    }

    @Override
    public void asyncSave(LogOperation logOperation) {
        Thread.startVirtualThread(() -> {
            try {
                logOperationMapper.insert(logOperation);
            } catch (Exception e) {
                log.error("异步保存操作日志失败: {}", e.getMessage(), e);
            }
        });
    }

}
