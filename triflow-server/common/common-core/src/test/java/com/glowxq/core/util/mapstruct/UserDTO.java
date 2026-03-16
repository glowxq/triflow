package com.glowxq.core.util.mapstruct;

import com.glowxq.common.core.config.StrictTypeMapping;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试用户 DTO（用于创建/更新）
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AutoMapper(target = User.class, mappingControl = StrictTypeMapping.class)
public class UserDTO {

    /** 用户名 */
    private String username;

    /** 邮箱 */
    private String email;

    /** 年龄 */
    private Integer age;

    /** 状态 */
    private Integer status;

}
