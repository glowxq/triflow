package com.glowxq.triflow.base.file.mapper;

import com.glowxq.triflow.base.file.entity.FileInfo;
import com.glowxq.triflow.base.file.pojo.dto.FileInfoQueryDTO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文件信息数据访问层
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 根据MD5查询文件（用于秒传）
     */
    default FileInfo selectByMd5(String md5) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileInfo.class)
                                      .eq(FileInfo::getMd5, md5)
                                      .eq(FileInfo::getStatus, 1);
        return selectOneByQuery(qw);
    }

    /**
     * 根据业务类型和业务ID查询文件列表
     */
    default List<FileInfo> selectByBiz(String bizType, Long bizId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileInfo.class)
                                      .eq(FileInfo::getBizType, bizType)
                                      .eq(FileInfo::getBizId, bizId)
                                      .eq(FileInfo::getStatus, 1)
                                      .orderBy(FileInfo::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 根据上传者ID查询文件列表
     */
    default List<FileInfo> selectByUploaderId(Long uploaderId) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileInfo.class)
                                      .eq(FileInfo::getUploaderId, uploaderId)
                                      .eq(FileInfo::getStatus, 1)
                                      .orderBy(FileInfo::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 根据分类查询文件列表
     */
    default List<FileInfo> selectByCategory(String category) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileInfo.class)
                                      .eq(FileInfo::getCategory, category)
                                      .eq(FileInfo::getStatus, 1)
                                      .orderBy(FileInfo::getCreateTime, false);
        return selectListByQuery(qw);
    }

    /**
     * 根据查询条件查询文件列表
     */
    default List<FileInfo> selectByQuery(FileInfoQueryDTO query) {
        return selectListByQuery(buildQueryWrapper(query));
    }

    /**
     * 根据查询条件分页查询文件
     */
    default Page<FileInfo> paginateByQuery(int pageNum, int pageSize, FileInfoQueryDTO query) {
        return paginate(pageNum, pageSize, buildQueryWrapper(query));
    }

    /**
     * 构建查询条件
     */
    default QueryWrapper buildQueryWrapper(FileInfoQueryDTO query) {
        QueryWrapper qw = QueryWrapper.create()
                                      .from(FileInfo.class)
                                      .like(FileInfo::getOriginalName, query.getKeyword(), StringUtils.isNotBlank(query.getKeyword()))
                                      .eq(FileInfo::getCategory, query.getCategory(), StringUtils.isNotBlank(query.getCategory()))
                                      .eq(FileInfo::getFileExt, query.getFileExt(), StringUtils.isNotBlank(query.getFileExt()))
                                      .eq(FileInfo::getBizType, query.getBizType(), StringUtils.isNotBlank(query.getBizType()))
                                      .eq(FileInfo::getBizId, query.getBizId(), query.getBizId() != null)
                                      .eq(FileInfo::getUploaderId, query.getUploaderId(), query.getUploaderId() != null)
                                      .eq(FileInfo::getStatus, query.getStatus(), query.getStatus() != null)
                                      .ge(FileInfo::getCreateTime, query.getStartTime(), query.getStartTime() != null)
                                      .le(FileInfo::getCreateTime, query.getEndTime(), query.getEndTime() != null)
                                      .orderBy(FileInfo::getCreateTime, false);
        return qw;
    }

    /**
     * 增加下载次数
     */
    default int incrementDownloadCount(Long id) {
        FileInfo file = selectOneById(id);
        if (file == null) {
            return 0;
        }
        FileInfo update = new FileInfo();
        update.setId(id);
        update.setDownloadCount(file.getDownloadCount() == null ? 1 : file.getDownloadCount() + 1);
        return update(update);
    }

}
