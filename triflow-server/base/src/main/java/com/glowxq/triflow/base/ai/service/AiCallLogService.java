package com.glowxq.triflow.base.ai.service;

import com.glowxq.triflow.base.ai.pojo.query.AiCallLogQuery;
import com.glowxq.triflow.base.ai.pojo.vo.AiCallLogVO;
import com.mybatisflex.core.paginate.Page;

/**
 * AI 调用记录服务接口
 *
 * @author glowxq
 * @since 2025-01-29
 */
public interface AiCallLogService {

    /**
     * 分页查询 AI 调用记录
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<AiCallLogVO> page(AiCallLogQuery query);

    /**
     * 获取记录详情
     *
     * @param id 记录ID
     * @return 记录详情
     */
    AiCallLogVO getById(Long id);

    /**
     * 保存调用记录
     *
     * @param userId           用户ID
     * @param username         用户名
     * @param provider         AI 提供商
     * @param model            模型
     * @param systemPrompt     系统提示
     * @param userMessage      用户消息
     * @param aiResponse       AI 响应
     * @param promptTokens     提示 token 数
     * @param completionTokens 完成 token 数
     * @param totalTokens      总 token 数
     * @param duration         耗时 (毫秒)
     * @param status           状态
     * @param errorMessage     错误信息
     * @param ip               IP 地址
     */
    void saveLog(Long userId, String username, String provider, String model,
                 String systemPrompt, String userMessage, String aiResponse,
                 Integer promptTokens, Integer completionTokens, Integer totalTokens,
                 Long duration, Integer status, String errorMessage, String ip);

    /**
     * 删除记录
     *
     * @param id 记录ID
     */
    void delete(Long id);
}
