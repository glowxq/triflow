package com.glowxq.common.oss.client;

import com.glowxq.common.oss.config.S3Configuration;
import com.glowxq.common.oss.enums.AccessControlType;
import com.glowxq.common.oss.model.*;
import com.glowxq.common.oss.support.FileKeyGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.Upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * S3 操作模板
 * <p>
 * 基于 S3 标准的对象存储操作统一入口，提供以下功能：
 * </p>
 *
 * <h3>1. 预签名前端直传</h3>
 * <ul>
 *     <li>{@link #generatePresignedUploadUrl(String)} - 生成预签名上传 URL</li>
 *     <li>{@link #generatePresignedDownloadUrl(String)} - 生成预签名下载 URL</li>
 * </ul>
 *
 * <h3>2. 服务端上传</h3>
 * <ul>
 *     <li>{@link #upload(MultipartFile)} - 上传 MultipartFile</li>
 *     <li>{@link #upload(File, String)} - 上传本地文件</li>
 *     <li>{@link #upload(InputStream, String, long, String)} - 上传输入流</li>
 * </ul>
 *
 * <h3>3. STS 临时凭证</h3>
 * <ul>
 *     <li>{@link #generateStsCredentials(String)} - 生成 STS 临时凭证</li>
 * </ul>
 *
 * <h3>4. Bucket 管理</h3>
 * <ul>
 *     <li>{@link #createBucket(String)} - 创建存储桶</li>
 *     <li>{@link #deleteBucket(String)} - 删除存储桶</li>
 *     <li>{@link #setBucketAcl(String, AccessControlType)} - 设置存储桶访问权限</li>
 *     <li>{@link #listBuckets()} - 列出所有存储桶</li>
 * </ul>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3Template {

    private final S3Client s3Client;

    private final S3AsyncClient s3AsyncClient;

    private final S3Presigner s3Presigner;

    private final S3TransferManager transferManager;

    private final StsClient stsClient;

    private final S3Configuration properties;

    private final FileKeyGenerator fileKeyGenerator;

    // ==================== 预签名前端直传 ====================

    /**
     * 生成预签名上传 URL
     * <p>
     * 客户端可使用此 URL 直接上传文件到 OSS，无需经过服务端。
     * </p>
     *
     * @param originalFilename 原始文件名
     *
     * @return 预签名上传结果
     */
    public PresignedUploadResult generatePresignedUploadUrl(String originalFilename) {
        return generatePresignedUploadUrl(null, originalFilename, null);
    }

    /**
     * 生成预签名上传 URL
     *
     * @param prefix           路径前缀
     * @param originalFilename 原始文件名
     * @param contentType      内容类型（可选）
     *
     * @return 预签名上传结果
     */
    public PresignedUploadResult generatePresignedUploadUrl(String prefix, String originalFilename, String contentType) {
        return generatePresignedUploadUrl(properties.getBucketName(), prefix, originalFilename, contentType,
                properties.getPresignedUrlExpireMinutes());
    }

    /**
     * 生成预签名上传 URL（完整参数）
     *
     * @param bucketName       存储桶名称
     * @param prefix           路径前缀
     * @param originalFilename 原始文件名
     * @param contentType      内容类型
     * @param expireMinutes    有效期（分钟）
     *
     * @return 预签名上传结果
     */
    public PresignedUploadResult generatePresignedUploadUrl(String bucketName, String prefix,
                                                            String originalFilename, String contentType,
                                                            int expireMinutes) {
        String objectKey = fileKeyGenerator.generate(prefix, originalFilename);

        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                                                                  .bucket(bucketName)
                                                                  .key(objectKey);

        if (contentType != null && !contentType.isBlank()) {
            requestBuilder.contentType(contentType);
        }

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                                                                        .putObjectRequest(requestBuilder.build())
                                                                        .signatureDuration(Duration.ofMinutes(expireMinutes))
                                                                        .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return PresignedUploadResult.builder()
                                    .uploadUrl(presignedRequest.url().toString())
                                    .objectKey(objectKey)
                                    .accessUrl(buildAccessUrl(bucketName, objectKey))
                                    .bucketName(bucketName)
                                    .expiresInSeconds((long) expireMinutes * 60)
                                    .build();
    }

    /**
     * 生成预签名下载 URL
     * <p>
     * 用于私有文件的临时访问。
     * </p>
     *
     * @param objectKey 对象键
     *
     * @return 预签名下载 URL
     */
    public String generatePresignedDownloadUrl(String objectKey) {
        return generatePresignedDownloadUrl(properties.getBucketName(), objectKey, 3600);
    }

    /**
     * 生成预签名下载 URL
     *
     * @param bucketName    存储桶名称
     * @param objectKey     对象键
     * @param expireSeconds 有效期（秒）
     *
     * @return 预签名下载 URL
     */
    public String generatePresignedDownloadUrl(String bucketName, String objectKey, int expireSeconds) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                                                            .bucket(bucketName)
                                                            .key(objectKey)
                                                            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                                                                        .getObjectRequest(getObjectRequest)
                                                                        .signatureDuration(Duration.ofSeconds(expireSeconds))
                                                                        .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    // ==================== 服务端上传 ====================

    /**
     * 上传 MultipartFile
     *
     * @param file 上传的文件
     *
     * @return 上传结果
     */
    public UploadResult upload(MultipartFile file) throws IOException {
        return upload(null, file);
    }

    /**
     * 上传 MultipartFile 到指定路径前缀
     *
     * @param prefix 路径前缀
     * @param file   上传的文件
     *
     * @return 上传结果
     */
    public UploadResult upload(String prefix, MultipartFile file) throws IOException {
        String objectKey = fileKeyGenerator.generate(prefix, file.getOriginalFilename());
        return uploadStream(file.getInputStream(), objectKey, file.getSize(), file.getContentType(),
                file.getOriginalFilename());
    }

    /**
     * 上传本地文件
     *
     * @param file      本地文件
     * @param objectKey 对象键
     *
     * @return 上传结果
     */
    public UploadResult upload(File file, String objectKey) {
        return upload(properties.getBucketName(), file, objectKey);
    }

    /**
     * 上传本地文件到指定存储桶
     *
     * @param bucketName 存储桶名称
     * @param file       本地文件
     * @param objectKey  对象键
     *
     * @return 上传结果
     */
    public UploadResult upload(String bucketName, File file, String objectKey) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                                                       .bucket(bucketName)
                                                       .key(objectKey)
                                                       .build();

            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromFile(file));

            return UploadResult.builder()
                               .url(buildAccessUrl(bucketName, objectKey))
                               .objectKey(objectKey)
                               .originalFilename(file.getName())
                               .bucketName(bucketName)
                               .size(file.length())
                               .eTag(normalizeETag(response.eTag()))
                               .build();
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传输入流
     *
     * @param inputStream      输入流
     * @param objectKey        对象键
     * @param size             文件大小
     * @param contentType      内容类型
     * @param originalFilename 原始文件名
     *
     * @return 上传结果
     */
    public UploadResult upload(InputStream inputStream, String objectKey, long size,
                               String contentType, String originalFilename) {
        return uploadStream(inputStream, objectKey, size, contentType, originalFilename);
    }

    /**
     * 使用 TransferManager 流式上传（支持大文件）
     */
    private UploadResult uploadStream(InputStream inputStream, String objectKey, long size,
                                      String contentType, String originalFilename) {
        try {
            String bucketName = properties.getBucketName();
            BlockingInputStreamAsyncRequestBody body = AsyncRequestBody.forBlockingInputStream(size);

            Upload upload = transferManager.upload(builder -> builder
                    .requestBody(body)
                    .putObjectRequest(req -> req
                            .bucket(bucketName)
                            .key(objectKey)
                            .contentType(contentType)
                            .build())
                    .build());

            body.writeInputStream(inputStream);
            CompletedUpload result = upload.completionFuture().join();

            return UploadResult.builder()
                               .url(buildAccessUrl(bucketName, objectKey))
                               .objectKey(objectKey)
                               .originalFilename(originalFilename)
                               .bucketName(bucketName)
                               .size(size)
                               .contentType(contentType)
                               .eTag(normalizeETag(result.response().eTag()))
                               .build();
        } catch (Exception e) {
            log.error("流式上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    // ==================== 文件下载 ====================

    /**
     * 下载文件到输出流
     *
     * @param objectKey    对象键
     * @param outputStream 输出流
     */
    public void download(String objectKey, OutputStream outputStream) {
        download(properties.getBucketName(), objectKey, outputStream);
    }

    /**
     * 下载文件到输出流
     *
     * @param bucketName   存储桶名称
     * @param objectKey    对象键
     * @param outputStream 输出流
     */
    public void download(String bucketName, String objectKey, OutputStream outputStream) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                                                       .bucket(bucketName)
                                                       .key(objectKey)
                                                       .build();

            s3AsyncClient.getObject(request, AsyncResponseTransformer.toBytes())
                         .thenAccept(response -> {
                             try {
                                 outputStream.write(response.asByteArray());
                                 outputStream.flush();
                             } catch (IOException e) {
                                 throw new RuntimeException("写入输出流失败", e);
                             }
                         }).join();
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件下载失败: " + e.getMessage(), e);
        }
    }

    /**
     * 下载文件到 HTTP 响应
     *
     * @param objectKey    对象键
     * @param response     HTTP 响应
     * @param downloadName 下载显示的文件名
     */
    public void download(String objectKey, HttpServletResponse response, String downloadName) {
        try {
            String encodedFilename = URLEncoder.encode(downloadName, StandardCharsets.UTF_8)
                                               .replace("+", "%20");

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);

            download(objectKey, response.getOutputStream());
        } catch (IOException e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件下载失败: " + e.getMessage(), e);
        }
    }

    // ==================== STS 临时凭证 ====================

    /**
     * 生成 STS 临时凭证
     * <p>
     * 用于前端直传场景，支持自定义上传路径限制。
     * </p>
     *
     * @param pathPrefix 允许上传的路径前缀
     *
     * @return STS 临时凭证
     */
    public StsCredentials generateStsCredentials(String pathPrefix) {
        return generateStsCredentials(properties.getBucketName(), pathPrefix);
    }

    /**
     * 生成 STS 临时凭证
     *
     * @param bucketName 存储桶名称
     * @param pathPrefix 允许上传的路径前缀
     *
     * @return STS 临时凭证
     */
    public StsCredentials generateStsCredentials(String bucketName, String pathPrefix) {
        String roleArn = properties.getStsRoleArn();
        if (roleArn == null || roleArn.isBlank()) {
            throw new IllegalStateException("STS Role ARN 未配置，请检查 oss.sts-role-arn 配置");
        }

        String normalizedPrefix = normalizePathPrefix(pathPrefix);
        String policy = buildStsPolicy(bucketName, normalizedPrefix);

        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                                                               .roleArn(roleArn)
                                                               .roleSessionName(properties.getStsRoleSessionName())
                                                               .durationSeconds(properties.getStsTokenDurationSeconds())
                                                               .policy(policy)
                                                               .build();

        AssumeRoleResponse assumeRoleResponse = stsClient.assumeRole(assumeRoleRequest);
        Credentials credentials = assumeRoleResponse.credentials();

        long expiresInSeconds = Duration.between(Instant.now(), credentials.expiration()).getSeconds();

        return StsCredentials.builder()
                             .accessKeyId(credentials.accessKeyId())
                             .secretAccessKey(credentials.secretAccessKey())
                             .sessionToken(credentials.sessionToken())
                             .expiration(credentials.expiration())
                             .endpoint(properties.getEndpointUrl())
                             .region(properties.getRegion())
                             .bucketName(bucketName)
                             .allowedPathPrefix(normalizedPrefix)
                             .expiresInSeconds(expiresInSeconds)
                             .build();
    }

    /**
     * 构建 STS 策略 JSON
     * <p>
     * 限制临时凭证只能上传到指定的路径前缀下。
     * </p>
     */
    private String buildStsPolicy(String bucketName, String pathPrefix) {
        String resource = "arn:aws:s3:::" + bucketName + "/" + pathPrefix + "*";

        return """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Action": [
                                "s3:PutObject",
                                "s3:GetObject"
                            ],
                            "Resource": "%s"
                        }
                    ]
                }
                """.formatted(resource);
    }

    // ==================== Bucket 管理 ====================

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     *
     * @return 存储桶信息
     */
    public S3BucketInfo createBucket(String bucketName) {
        return createBucket(bucketName, AccessControlType.PRIVATE);
    }

    /**
     * 创建存储桶并设置访问权限
     *
     * @param bucketName    存储桶名称
     * @param accessControl 访问控制类型
     *
     * @return 存储桶信息
     */
    public S3BucketInfo createBucket(String bucketName, AccessControlType accessControl) {
        try {
            CreateBucketRequest createRequest = CreateBucketRequest.builder()
                                                                   .bucket(bucketName)
                                                                   .acl(accessControl.getS3Acl())
                                                                   .build();

            s3Client.createBucket(createRequest);

            configureBucketCors(bucketName);

            log.info("存储桶创建成功: {}", bucketName);

            return S3BucketInfo.builder()
                               .name(bucketName)
                               .creationDate(Instant.now())
                               .region(properties.getRegion())
                               .accessControl(accessControl)
                               .bucketUrl(buildBucketUrl(bucketName))
                               .build();
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
            log.info("存储桶已存在: {}", bucketName);
            return getS3BucketInfo(bucketName);
        } catch (Exception e) {
            log.error("创建存储桶失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建存储桶失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除存储桶
     * <p>
     * 注意：只能删除空的存储桶。
     * </p>
     *
     * @param bucketName 存储桶名称
     */
    public void deleteBucket(String bucketName) {
        try {
            DeleteBucketRequest request = DeleteBucketRequest.builder()
                                                             .bucket(bucketName)
                                                             .build();

            s3Client.deleteBucket(request);
            log.info("存储桶删除成功: {}", bucketName);
        } catch (Exception e) {
            log.error("删除存储桶失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除存储桶失败: " + e.getMessage(), e);
        }
    }

    /**
     * 设置存储桶访问权限
     *
     * @param bucketName    存储桶名称
     * @param accessControl 访问控制类型
     */
    public void setBucketAcl(String bucketName, AccessControlType accessControl) {
        try {
            PutBucketAclRequest request = PutBucketAclRequest.builder()
                                                             .bucket(bucketName)
                                                             .acl(accessControl.getS3Acl())
                                                             .build();

            s3Client.putBucketAcl(request);
            log.info("存储桶 {} 访问权限已设置为: {}", bucketName, accessControl.getDisplayName());
        } catch (Exception e) {
            log.error("设置存储桶访问权限失败: {}", e.getMessage(), e);
            throw new RuntimeException("设置存储桶访问权限失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     *
     * @return 是否存在
     */
    public boolean bucketExists(String bucketName) {
        try {
            HeadBucketRequest request = HeadBucketRequest.builder()
                                                         .bucket(bucketName)
                                                         .build();

            s3Client.headBucket(request);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

    /**
     * 获取存储桶信息
     *
     * @param bucketName 存储桶名称
     *
     * @return 存储桶信息
     */
    public S3BucketInfo getS3BucketInfo(String bucketName) {
        try {
            GetBucketAclResponse aclResponse = s3Client.getBucketAcl(
                    GetBucketAclRequest.builder().bucket(bucketName).build());

            AccessControlType accessControl = determineAccessControlType(aclResponse);

            return S3BucketInfo.builder()
                               .name(bucketName)
                               .region(properties.getRegion())
                               .accessControl(accessControl)
                               .bucketUrl(buildBucketUrl(bucketName))
                               .build();
        } catch (Exception e) {
            log.error("获取存储桶信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取存储桶信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 列出所有存储桶
     *
     * @return 存储桶列表
     */
    public List<S3BucketInfo> listBuckets() {
        try {
            ListBucketsResponse response = s3Client.listBuckets();

            return response.buckets().stream()
                           .map(bucket -> S3BucketInfo.builder()
                                                      .name(bucket.name())
                                                      .creationDate(bucket.creationDate())
                                                      .region(properties.getRegion())
                                                      .bucketUrl(buildBucketUrl(bucket.name()))
                                                      .build())
                           .toList();
        } catch (Exception e) {
            log.error("列出存储桶失败: {}", e.getMessage(), e);
            throw new RuntimeException("列出存储桶失败: " + e.getMessage(), e);
        }
    }

    // ==================== 对象操作 ====================

    /**
     * 检查对象是否存在
     *
     * @param objectKey 对象键
     *
     * @return 是否存在
     */
    public boolean objectExists(String objectKey) {
        return objectExists(properties.getBucketName(), objectKey);
    }

    /**
     * 检查对象是否存在
     *
     * @param bucketName 存储桶名称
     * @param objectKey  对象键
     *
     * @return 是否存在
     */
    public boolean objectExists(String bucketName, String objectKey) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                                                         .bucket(bucketName)
                                                         .key(objectKey)
                                                         .build();

            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    /**
     * 获取对象信息
     *
     * @param objectKey 对象键
     *
     * @return 对象信息
     */
    public ObjectInfo getObjectInfo(String objectKey) {
        return getObjectInfo(properties.getBucketName(), objectKey);
    }

    /**
     * 获取对象信息
     *
     * @param bucketName 存储桶名称
     * @param objectKey  对象键
     *
     * @return 对象信息
     */
    public ObjectInfo getObjectInfo(String bucketName, String objectKey) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                                                         .bucket(bucketName)
                                                         .key(objectKey)
                                                         .build();

            HeadObjectResponse response = s3Client.headObject(request);

            return ObjectInfo.builder()
                             .objectKey(objectKey)
                             .bucketName(bucketName)
                             .size(response.contentLength())
                             .contentType(response.contentType())
                             .eTag(normalizeETag(response.eTag()))
                             .lastModified(response.lastModified())
                             .url(buildAccessUrl(bucketName, objectKey))
                             .build();
        } catch (Exception e) {
            log.error("获取对象信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取对象信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除对象
     *
     * @param objectKey 对象键
     */
    public void deleteObject(String objectKey) {
        deleteObject(properties.getBucketName(), objectKey);
    }

    /**
     * 删除对象
     *
     * @param bucketName 存储桶名称
     * @param objectKey  对象键
     */
    public void deleteObject(String bucketName, String objectKey) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                                                             .bucket(bucketName)
                                                             .key(objectKey)
                                                             .build();

            s3Client.deleteObject(request);
            log.info("对象删除成功: {}/{}", bucketName, objectKey);
        } catch (Exception e) {
            log.error("删除对象失败: {}", e.getMessage(), e);
            throw new RuntimeException("删除对象失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量删除对象
     *
     * @param objectKeys 对象键列表
     */
    public void deleteObjects(List<String> objectKeys) {
        deleteObjects(properties.getBucketName(), objectKeys);
    }

    /**
     * 批量删除对象
     *
     * @param bucketName 存储桶名称
     * @param objectKeys 对象键列表
     */
    public void deleteObjects(String bucketName, List<String> objectKeys) {
        try {
            List<ObjectIdentifier> objectIdentifiers = objectKeys.stream()
                                                                 .map(key -> ObjectIdentifier.builder().key(key).build())
                                                                 .toList();

            Delete delete = Delete.builder()
                                  .objects(objectIdentifiers)
                                  .build();

            DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                                                               .bucket(bucketName)
                                                               .delete(delete)
                                                               .build();

            s3Client.deleteObjects(request);
            log.info("批量删除对象成功，共删除 {} 个对象", objectKeys.size());
        } catch (Exception e) {
            log.error("批量删除对象失败: {}", e.getMessage(), e);
            throw new RuntimeException("批量删除对象失败: " + e.getMessage(), e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 配置存储桶 CORS
     */
    private void configureBucketCors(String bucketName) {
        try {
            CORSRule corsRule = CORSRule.builder()
                                        .allowedHeaders("*")
                                        .allowedMethods("GET", "PUT", "POST", "DELETE", "HEAD")
                                        .allowedOrigins("*")
                                        .maxAgeSeconds(3600)
                                        .build();

            PutBucketCorsRequest corsRequest = PutBucketCorsRequest.builder()
                                                                   .bucket(bucketName)
                                                                   .corsConfiguration(CORSConfiguration.builder()
                                                                                                       .corsRules(corsRule)
                                                                                                       .build())
                                                                   .build();

            s3Client.putBucketCors(corsRequest);
            log.debug("存储桶 {} CORS 配置完成", bucketName);
        } catch (Exception e) {
            log.warn("配置存储桶 CORS 失败: {}", e.getMessage());
        }
    }

    /**
     * 根据 ACL 响应判断访问控制类型
     */
    private AccessControlType determineAccessControlType(GetBucketAclResponse aclResponse) {
        boolean hasPublicRead = aclResponse.grants().stream()
                                           .anyMatch(grant -> grant.grantee() != null &&
                                                   "http://acs.amazonaws.com/groups/global/AllUsers".equals(grant.grantee().uri()) &&
                                                   grant.permission() == Permission.READ);

        boolean hasPublicWrite = aclResponse.grants().stream()
                                            .anyMatch(grant -> grant.grantee() != null &&
                                                    "http://acs.amazonaws.com/groups/global/AllUsers".equals(grant.grantee().uri()) &&
                                                    grant.permission() == Permission.WRITE);

        if (hasPublicRead && hasPublicWrite) {
            return AccessControlType.PUBLIC_READ_WRITE;
        } else if (hasPublicRead) {
            return AccessControlType.PUBLIC_READ;
        }
        return AccessControlType.PRIVATE;
    }

    /**
     * 构建文件访问 URL
     */
    private String buildAccessUrl(String bucketName, String objectKey) {
        String domain = properties.getDomain();
        if (domain != null && !domain.isBlank()) {
            String baseUrl = domain.endsWith("/") ? domain.substring(0, domain.length() - 1) : domain;
            return baseUrl + "/" + objectKey;
        }

        boolean pathStyle = properties.getProvider().isPathStyleAccess();
        String scheme = properties.isUseHttps() ? "https://" : "http://";
        String endpoint = properties.getEndpoint();

        if (pathStyle) {
            return scheme + endpoint + "/" + bucketName + "/" + objectKey;
        } else {
            return scheme + bucketName + "." + endpoint + "/" + objectKey;
        }
    }

    /**
     * 构建存储桶 URL
     */
    private String buildBucketUrl(String bucketName) {
        boolean pathStyle = properties.getProvider().isPathStyleAccess();
        String scheme = properties.isUseHttps() ? "https://" : "http://";
        String endpoint = properties.getEndpoint();

        if (pathStyle) {
            return scheme + endpoint + "/" + bucketName;
        } else {
            return scheme + bucketName + "." + endpoint;
        }
    }

    /**
     * 规范化路径前缀
     */
    private String normalizePathPrefix(String pathPrefix) {
        if (pathPrefix == null || pathPrefix.isBlank()) {
            return "";
        }
        String normalized = pathPrefix.trim();
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (!normalized.endsWith("/")) {
            normalized = normalized + "/";
        }
        return normalized;
    }

    /**
     * 规范化 ETag
     */
    private String normalizeETag(String eTag) {
        if (eTag == null) {
            return null;
        }
        return eTag.replace("\"", "");
    }

}
