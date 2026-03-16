package com.glowxq.core.util.mapstruct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 测试用户实体
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 邮箱 */
    private String email;

    /** 年龄 */
    private Integer age;

    /** 状态（0:禁用, 1:正常） */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

}
