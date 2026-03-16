package com.glowxq.triflow.base.file.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.oss.client.S3Template;
import com.glowxq.common.oss.model.PresignedUploadResult;
import com.glowxq.triflow.base.file.entity.FileInfo;
import com.glowxq.triflow.base.file.enums.FileCategoryEnum;
import com.glowxq.triflow.base.file.mapper.FileInfoMapper;
import com.glowxq.triflow.base.file.pojo.dto.ConfirmUploadDTO;
import com.glowxq.triflow.base.file.pojo.dto.FileInfoQueryDTO;
import com.glowxq.triflow.base.file.pojo.dto.PresignRequestDTO;
import com.glowxq.triflow.base.file.pojo.vo.FileInfoVO;
import com.glowxq.triflow.base.file.service.FileInfoService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件信息服务实现类
 * <p>
 * 支持 S3 兼容存储的预签名直传。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileInfoServiceImpl implements FileInfoService {

    private final FileInfoMapper fileInfoMapper;

    private final S3Template s3Template;

    @Override
    public FileInfoVO getById(Long id) {
        FileInfo file = fileInfoMapper.selectOneById(id);
        if (file == null) {
            return null;
        }
        return MapStructUtils.convert(file, FileInfoVO.class);
    }

    @Override
    public FileInfo getByMd5(String md5) {
        return fileInfoMapper.selectByMd5(md5);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        int rows = fileInfoMapper.deleteById(id);
        log.info("删除文件信息, id={}, affected={}", id, rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        for (Long id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public List<FileInfoVO> list(FileInfoQueryDTO query) {
        List<FileInfo> files = fileInfoMapper.selectByQuery(query);
        return MapStructUtils.convert(files, FileInfoVO.class);
    }

    @Override
    public Page<FileInfoVO> page(FileInfoQueryDTO query) {
        QueryWrapper qw = fileInfoMapper.buildQueryWrapper(query);
        Page<FileInfo> filePage = fileInfoMapper.paginate(query.buildPage(), qw);

        List<FileInfoVO> records = MapStructUtils.convert(filePage.getRecords(), FileInfoVO.class);
        // 为每个记录填充预签名预览URL
        for (int i = 0; i < records.size(); i++) {
            FileInfoVO vo = records.get(i);
            FileInfo entity = filePage.getRecords().get(i);
            if (StringUtils.isNotBlank(entity.getObjectKey())) {
                vo.setPreviewUrl(s3Template.generatePresignedDownloadUrl(entity.getObjectKey()));
            }
        }
        return new Page<>(records, filePage.getPageNumber(), filePage.getPageSize(), filePage.getTotalRow());
    }

    @Override
    public List<FileInfoVO> listByBiz(String bizType, Long bizId) {
        List<FileInfo> files = fileInfoMapper.selectByBiz(bizType, bizId);
        return MapStructUtils.convert(files, FileInfoVO.class);
    }

    @Override
    public List<FileInfoVO> listByUploaderId(Long uploaderId) {
        List<FileInfo> files = fileInfoMapper.selectByUploaderId(uploaderId);
        return MapStructUtils.convert(files, FileInfoVO.class);
    }

    @Override
    public List<FileInfoVO> listByCategory(String category) {
        List<FileInfo> files = fileInfoMapper.selectByCategory(category);
        return MapStructUtils.convert(files, FileInfoVO.class);
    }

    @Override
    public void incrementDownloadCount(Long id) {
        fileInfoMapper.incrementDownloadCount(id);
    }

    @Override
    public PresignedUploadResult generatePresignedUrl(PresignRequestDTO dto) {
        // 根据业务类型确定存储路径前缀
        String prefix = StringUtils.defaultIfBlank(dto.getBizType(), "uploads");

        // 调用 S3Template 生成预签名 URL
        PresignedUploadResult result = s3Template.generatePresignedUploadUrl(
                prefix,
                dto.getOriginalName(),
                dto.getContentType()
        );

        log.info("生成预签名URL: objectKey={}, expiresInSeconds={}",
                result.getObjectKey(), result.getExpiresInSeconds());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfoVO confirmUpload(ConfirmUploadDTO dto) {
        // 从文件名获取扩展名
        String fileExt = getFileExtension(dto.getOriginalName());
        // 优先使用前端传入的 contentType，否则根据扩展名推断
        String fileType = StringUtils.isNotBlank(dto.getContentType())
                ? dto.getContentType()
                : getFileType(fileExt);

        // 创建文件信息记录
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalName(dto.getOriginalName());
        fileInfo.setFileName(extractFileName(dto.getObjectKey()));
        fileInfo.setFilePath(dto.getObjectKey());
        fileInfo.setFileUrl(dto.getAccessUrl());
        fileInfo.setFileSize(dto.getFileSize());
        fileInfo.setFileType(fileType);
        fileInfo.setFileExt(fileExt);
        fileInfo.setStorageType("oss");
        fileInfo.setBucketName(dto.getBucketName());
        fileInfo.setObjectKey(dto.getObjectKey());
        // 根据 MIME type 自动推断文件分类
        fileInfo.setCategory(inferCategory(fileType));
        fileInfo.setBizType(dto.getBizType());
        fileInfo.setBizId(dto.getBizId());
        fileInfo.setConfigId(dto.getConfigId());
        fileInfo.setUploadTime(LocalDateTime.now());
        fileInfo.setStatus(1);

        fileInfoMapper.insert(fileInfo);

        log.info("确认上传成功: id={}, originalName={}, objectKey={}, category={}",
                fileInfo.getId(), dto.getOriginalName(), dto.getObjectKey(), fileInfo.getCategory());

        FileInfoVO vo = MapStructUtils.convert(fileInfo, FileInfoVO.class);
        // 生成预签名预览 URL
        if (StringUtils.isNotBlank(fileInfo.getObjectKey())) {
            vo.setPreviewUrl(s3Template.generatePresignedDownloadUrl(fileInfo.getObjectKey()));
        }
        return vo;
    }

    @Override
    public String getPreviewUrl(Long id) {
        FileInfo file = fileInfoMapper.selectOneById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        // 如果存储在 OSS，使用预签名下载 URL
        if ("oss".equals(file.getStorageType()) && StringUtils.isNotBlank(file.getObjectKey())) {
            return s3Template.generatePresignedDownloadUrl(file.getObjectKey());
        }

        // 否则返回直接访问 URL
        return file.getFileUrl();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean physicalDeleteById(Long id) {
        FileInfo file = fileInfoMapper.selectOneById(id);
        if (file == null) {
            log.warn("物理删除文件失败, 文件不存在, id={}", id);
            return false;
        }

        // 删除 OSS 文件
        if ("oss".equals(file.getStorageType()) && StringUtils.isNotBlank(file.getObjectKey())) {
            try {
                s3Template.deleteObject(file.getObjectKey());
                log.info("删除OSS文件成功, objectKey={}", file.getObjectKey());
            } catch (Exception e) {
                log.error("删除OSS文件失败, objectKey={}, error={}", file.getObjectKey(), e.getMessage());
                throw new BusinessException("删除OSS文件失败: " + e.getMessage());
            }
        }

        // 物理删除数据库记录（绕过逻辑删除）
        int rows = fileInfoMapper.deleteById(id);
        log.info("物理删除文件信息, id={}, originalName={}, affected={}", id, file.getOriginalName(), rows);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean physicalDeleteByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }

        // 查询所有文件信息
        List<FileInfo> files = fileInfoMapper.selectListByIds(ids);
        if (CollectionUtils.isEmpty(files)) {
            log.warn("批量物理删除文件失败, 文件不存在, ids={}", ids);
            return false;
        }

        // 收集需要删除的 OSS objectKey
        List<String> objectKeys = files.stream()
                .filter(f -> "oss".equals(f.getStorageType()) && StringUtils.isNotBlank(f.getObjectKey()))
                .map(FileInfo::getObjectKey)
                .toList();

        // 批量删除 OSS 文件
        if (!objectKeys.isEmpty()) {
            try {
                s3Template.deleteObjects(objectKeys);
                log.info("批量删除OSS文件成功, count={}, objectKeys={}", objectKeys.size(), objectKeys);
            } catch (Exception e) {
                log.error("批量删除OSS文件失败, objectKeys={}, error={}", objectKeys, e.getMessage());
                throw new BusinessException("批量删除OSS文件失败: " + e.getMessage());
            }
        }

        // 物理删除数据库记录
        for (Long id : ids) {
            fileInfoMapper.deleteById(id);
        }
        log.info("批量物理删除文件信息, ids={}", ids);
        return true;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 从 objectKey 中提取文件名
     */
    private String extractFileName(String objectKey) {
        if (StringUtils.isBlank(objectKey)) {
            return "";
        }
        int lastSlash = objectKey.lastIndexOf("/");
        return lastSlash >= 0 ? objectKey.substring(lastSlash + 1) : objectKey;
    }

    /**
     * 根据扩展名推断文件类型
     */
    private String getFileType(String ext) {
        if (StringUtils.isBlank(ext)) {
            return "other";
        }
        return switch (ext.toLowerCase()) {
            case "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg" -> "image";
            case "mp4", "avi", "mkv", "mov", "wmv" -> "video";
            case "mp3", "wav", "flac", "aac", "ogg" -> "audio";
            case "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt" -> "document";
            case "zip", "rar", "7z", "tar", "gz" -> "archive";
            default -> "other";
        };
    }


    /**
     * 根据 MIME type 或简单类型推断文件分类
     * @param fileType MIME 类型（如 image/png）或简单类型（如 image）
     * @return 文件分类（对应 FileCategoryEnum）
     */
    private String inferCategory(String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return FileCategoryEnum.OTHER.getCode();
        }
        String lowerType = fileType.toLowerCase();

        // 支持 MIME type 格式（image/png）和简单格式（image）
        if (lowerType.startsWith("image/") || lowerType.equals("image")) {
            return FileCategoryEnum.IMAGE.getCode();
        }
        if (lowerType.startsWith("video/") || lowerType.equals("video")) {
            return FileCategoryEnum.VIDEO.getCode();
        }
        if (lowerType.startsWith("audio/") || lowerType.equals("audio")) {
            return FileCategoryEnum.AUDIO.getCode();
        }
        // 文档类型
        if (lowerType.equals("document") ||
            lowerType.contains("pdf") ||
            lowerType.contains("word") ||
            lowerType.contains("document") ||
            lowerType.contains("excel") ||
            lowerType.contains("sheet") ||
            lowerType.contains("powerpoint") ||
            lowerType.contains("presentation") ||
            lowerType.startsWith("text/") ||
            lowerType.contains("markdown")) {
            return FileCategoryEnum.DOCUMENT.getCode();
        }
        // 压缩包
        if (lowerType.equals("archive") ||
            lowerType.contains("zip") ||
            lowerType.contains("rar") ||
            lowerType.contains("7z") ||
            lowerType.contains("tar") ||
            lowerType.contains("gzip") ||
            lowerType.contains("compressed")) {
            return "archive";
        }
        return FileCategoryEnum.OTHER.getCode();
    }

}
