package com.glowxq.common.core.common.enums.base;

/**
 * 基础枚举接口
 * <p>
 * 所有业务枚举类的基础接口，定义统一的获取 code 和 name 的方法。
 * 实现此接口的枚举类可以通过通用方法进行枚举值的查找和转换。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * @Getter
 * @AllArgsConstructor
 * public enum StatusEnum implements BaseEnum {
 *     ENABLED("1", "启用"),
 *     DISABLED("0", "禁用");
 *
 *     private final String code;
 *     private final String name;
 * }
 * }</pre>
 *
 * @author glowxq
 * @version 1.1
 * @since 2023/10/11
 */
public interface BaseEnum {

    /**
     * 获取枚举编码
     * <p>
     * 用于数据库存储、接口传输等场景的唯一标识符。
     * </p>
     *
     * @return 枚举编码
     */
    String getCode();

    /**
     * 获取枚举名称
     * <p>
     * 用于前端展示、日志记录等场景的可读描述。
     * </p>
     *
     * @return 枚举名称/描述
     */
    String getName();
}
