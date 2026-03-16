package com.glowxq.core.util.mapstruct;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 测试用户 VO（用于响应）
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AutoMapper(target = User.class)
public class UserVO {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 邮箱 */
    private String email;

    /** 年龄 */
    private Integer age;

    /** 状态 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

}
