package com.glowxq.common.oss.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.net.URI;

/**
 * S3 客户端自动配置
 * <p>
 * 根据配置自动创建 S3 相关的客户端 Bean，包括：
 * <ul>
 *     <li>S3Client - 同步客户端</li>
 *     <li>S3AsyncClient - 异步客户端</li>
 *     <li>S3Presigner - 预签名 URL 生成器</li>
 *     <li>S3TransferManager - 传输管理器</li>
 *     <li>StsClient - STS 客户端（用于临时凭证）</li>
 * </ul>
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(S3Configuration.class)
@ConditionalOnProperty(prefix = "oss", name = "enabled", havingValue = "true", matchIfMissing = true)
public class S3ClientConfiguration {

    private final S3Configuration properties;

    /**
     * 创建 S3 同步客户端
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client() {
        log.info("初始化 S3 同步客户端，提供商: {}，端点: {}",
                properties.getProvider().getDisplayName(),
                properties.getEndpoint());

        return S3Client.builder()
                       .region(getRegion())
                       .credentialsProvider(credentialsProvider())
                       .endpointOverride(getEndpointUri())
                       .forcePathStyle(properties.getProvider().isPathStyleAccess())
                       .build();
    }

    /**
     * 创建 S3 异步客户端
     * <p>
     * 使用 CRT (Common Runtime) 客户端以获得更好的性能。
     * </p>
     */
    @Bean
    @ConditionalOnMissingBean
    public S3AsyncClient s3AsyncClient() {
        log.info("初始化 S3 异步客户端（CRT）");

        return S3AsyncClient.crtBuilder()
                            .region(getRegion())
                            .credentialsProvider(credentialsProvider())
                            .endpointOverride(getEndpointUri())
                            .forcePathStyle(properties.getProvider().isPathStyleAccess())
                            .targetThroughputInGbps(20.0)
                            .checksumValidationEnabled(false)
                            .build();
    }

    /**
     * 创建 S3 传输管理器
     * <p>
     * 用于高效的大文件上传下载，支持分片上传和并行传输。
     * </p>
     */
    @Bean
    @ConditionalOnMissingBean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        log.info("初始化 S3 传输管理器");

        return S3TransferManager.builder()
                                .s3Client(s3AsyncClient)
                                .build();
    }

    /**
     * 创建 S3 预签名 URL 生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Presigner s3Presigner() {
        log.info("初始化 S3 预签名器, region={}, endpoint={}",
                properties.getRegion(), getEndpointUri());

        software.amazon.awssdk.services.s3.S3Configuration s3Config = software.amazon.awssdk.services.s3.S3Configuration.builder()
                                                                                                                        .pathStyleAccessEnabled(properties.getProvider().isPathStyleAccess())
                                                                                                                        .chunkedEncodingEnabled(false)
                                                                                                                        .build();

        return S3Presigner.builder()
                          .region(getRegion())
                          .credentialsProvider(credentialsProvider())
                          .endpointOverride(getEndpointUri())
                          .serviceConfiguration(s3Config)
                          .build();
    }

    /**
     * 创建 STS 客户端
     * <p>
     * 用于获取临时安全凭证，支持前端直传场景。
     * </p>
     */
    @Bean
    @ConditionalOnMissingBean
    public StsClient stsClient() {
        log.info("初始化 STS 客户端");

        return StsClient.builder()
                        .region(getRegion())
                        .credentialsProvider(credentialsProvider())
                        .build();
    }

    /**
     * 获取区域配置
     */
    private Region getRegion() {
        return Region.of(properties.getRegion());
    }

    /**
     * 获取端点 URI
     */
    private URI getEndpointUri() {
        return URI.create(properties.getEndpointUrl());
    }

    /**
     * 创建凭证提供者
     */
    private StaticCredentialsProvider credentialsProvider() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.getAccessKey(),
                properties.getSecretKey()
        );
        return StaticCredentialsProvider.create(credentials);
    }

}
