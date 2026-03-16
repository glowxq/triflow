package com.glowxq.common.core.common.entity;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页查询基础类
 * <p>
 * 所有需要分页的查询 DTO 都应该继承此类，提供统一的分页参数定义。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * public class UserQuery extends PageQuery {
 *     private String username;
 *     private String status;
 * }
 * }</pre>
 *
 * @author glowxq
 * @version 1.0
 * @since 2022/8/25
 */
@Data
public class PageQuery {

    /**
     * 当前页码（从 1 开始）
     */
    @Schema(description = "页码", example = "1")
    @Min(value = 1L)
    private Integer pageNum = 1;

    /**
     * 每页记录数（建议不超过 100）
     */
    @Schema(description = "每页条数", example = "10")
    @Max(value = 1000L)
    private Integer pageSize = 10;

    public <T> Page<T> buildPage() {
        return new Page<>(pageNum, pageSize);
    }
}
