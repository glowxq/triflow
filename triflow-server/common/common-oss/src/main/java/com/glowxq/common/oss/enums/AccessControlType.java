package com.glowxq.common.oss.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.BucketCannedACL;

/**
 * Bucket 访问控制类型枚举
 * <p>
 * 定义 Bucket 的访问权限级别，遵循 S3 标准 ACL。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Getter
@RequiredArgsConstructor
public enum AccessControlType {

    /**
     * 私有访问
     * <p>
     * 只有 Bucket 所有者拥有完全控制权限，
     * 其他用户无法访问。
     * </p>
     */
    PRIVATE("私有", BucketCannedACL.PRIVATE),

    /**
     * 公共读
     * <p>
     * 任何人都可以读取 Bucket 中的对象，
     * 但只有所有者可以写入。
     * </p>
     */
    PUBLIC_READ("公共读", BucketCannedACL.PUBLIC_READ),

    /**
     * 公共读写
     * <p>
     * 任何人都可以读取和写入 Bucket 中的对象。
     * 警告：此设置存在安全风险，请谨慎使用。
     * </p>
     */
    PUBLIC_READ_WRITE("公共读写", BucketCannedACL.PUBLIC_READ_WRITE);

    /**
     * 显示名称
     */
    private final String displayName;

    /**
     * S3 ACL 枚举值
     */
    private final BucketCannedACL s3Acl;
}
