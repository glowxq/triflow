package com.glowxq.triflow.base.file.service;

import com.glowxq.common.oss.model.PresignedUploadResult;
import com.glowxq.triflow.base.file.entity.FileInfo;
import com.glowxq.triflow.base.file.pojo.dto.ConfirmUploadDTO;
import com.glowxq.triflow.base.file.pojo.dto.FileInfoQueryDTO;
import com.glowxq.triflow.base.file.pojo.dto.PresignRequestDTO;
import com.glowxq.triflow.base.file.pojo.vo.FileInfoVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 文件信息服务接口
 *
 * @author glowxq
 * @since 2025-01-24
 */
public interface FileInfoService {

    FileInfoVO getById(Long id);

    FileInfo getByMd5(String md5);

    boolean deleteById(Long id);

    boolean deleteByIds(List<Long> ids);

    List<FileInfoVO> list(FileInfoQueryDTO query);

    Page<FileInfoVO> page(FileInfoQueryDTO query);

    List<FileInfoVO> listByBiz(String bizType, Long bizId);

    List<FileInfoVO> listByUploaderId(Long uploaderId);

    List<FileInfoVO> listByCategory(String category);

    void incrementDownloadCount(Long id);

    /**
     * 生成预签名上传URL
     * <p>
     * 前端使用此URL直传文件到OSS，不经过服务端。
     * </p>
     *
     * @param dto 预签名请求参数
     * @return 预签名上传结果
     */
    PresignedUploadResult generatePresignedUrl(PresignRequestDTO dto);

    /**
     * 确认上传完成
     * <p>
     * 前端直传OSS成功后，调用此方法保存文件记录到数据库。
     * </p>
     *
     * @param dto 确认上传参数
     * @return 文件信息
     */
    FileInfoVO confirmUpload(ConfirmUploadDTO dto);

    /**
     * 获取文件预览URL
     * <p>
     * 对于私有存储，返回预签名下载URL。
     * </p>
     *
     * @param id 文件ID
     * @return 预览URL
     */
    String getPreviewUrl(Long id);


    /**
     * 物理删除文件（同时删除OSS文件）
     *
     * @param id 文件ID
     * @return 是否删除成功
     */
    boolean physicalDeleteById(Long id);

    /**
     * 批量物理删除文件（同时删除OSS文件）
     *
     * @param ids ID列表
     * @return 是否删除成功
     */
    boolean physicalDeleteByIds(List<Long> ids);

}
