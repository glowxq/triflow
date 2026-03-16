package com.glowxq.common.core.common.enums;

/**
 * 错误码接口
 * <p>
 * 所有错误码枚举都应实现此接口，用于统一错误码的获取方式。
 * 错误码格式：TMMCCC (6位数字)
 * </p>
 * <ul>
 *   <li>T: 错误类型 (1位) - 1:业务异常, 2:告警异常, 3:客户端异常</li>
 *   <li>MM: 模块编码 (2位, 00-99)</li>
 *   <li>CCC: 错误序号 (3位, 000-999)</li>
 * </ul>
 *
 * @author glowxq
 * @version 2.0
 * @since 2025/1/21
 */
public interface IErrorCode {

    /**
     * 错误类型：业务异常
     * <p>仅记录日志，不发送告警</p>
     */
    int TYPE_BUSINESS = 1;

    /**
     * 错误类型：告警异常
     * <p>记录日志并发送告警通知（飞书/钉钉等）</p>
     */
    int TYPE_ALERT = 2;

    /**
     * 错误类型：客户端处理异常
     * <p>需要前端/客户端进行特殊处理</p>
     */
    int TYPE_CLIENT = 3;

    /**
     * 构建错误码
     * <p>工具方法：根据类型、模块、序号构建6位错误码</p>
     *
     * @param type   错误类型 (1/2/3)
     * @param module 模块编码 (00-99)
     * @param seq    错误序号 (000-999)
     *
     * @return 6位数字错误码
     */
    static int buildCode(int type, int module, int seq) {
        return type * 100000 + module * 1000 + seq;
    }

    /**
     * 获取错误码
     *
     * @return 6位数字错误码
     */
    int getCode();

    /**
     * 获取错误消息
     *
     * @return 错误消息描述
     */
    String getMessage();

    /**
     * 获取错误类型
     * <p>通过错误码首位数字判断</p>
     *
     * @return 错误类型 (1/2/3)
     */
    default int getType() {
        return getCode() / 100000;
    }

    /**
     * 获取模块编码
     * <p>通过错误码中间两位数字获取</p>
     *
     * @return 模块编码 (00-99)
     */
    default int getModule() {
        return (getCode() % 100000) / 1000;
    }

    /**
     * 判断是否为业务异常
     *
     * @return true 如果是业务异常
     */
    default boolean isBusinessError() {
        return getType() == TYPE_BUSINESS;
    }

    /**
     * 判断是否为告警异常
     *
     * @return true 如果是告警异常
     */
    default boolean isAlertError() {
        return getType() == TYPE_ALERT;
    }

    /**
     * 判断是否为客户端处理异常
     *
     * @return true 如果是客户端处理异常
     */
    default boolean isClientError() {
        return getType() == TYPE_CLIENT;
    }

}
