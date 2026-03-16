package com.glowxq.triflow.base.file.service;

import com.glowxq.triflow.base.file.entity.FileConfig;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigCreateDTO;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigQueryDTO;
import com.glowxq.triflow.base.file.pojo.dto.FileConfigUpdateDTO;
import com.glowxq.triflow.base.file.pojo.vo.FileConfigVO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * 文件存储配置服务接口
 *
 * @author glowxq
 * @since 2025-01-24
 */
public interface FileConfigService {

    Long create(FileConfigCreateDTO dto);

    boolean update(FileConfigUpdateDTO dto);

    FileConfigVO getById(Long id);

    FileConfig getByConfigKey(String configKey);

    FileConfig getDefaultConfig();

    boolean deleteById(Long id);

    boolean deleteByIds(List<Long> ids);

    List<FileConfigVO> list(FileConfigQueryDTO query);

    Page<FileConfigVO> page(FileConfigQueryDTO query);

    List<FileConfigVO> listAllEnabled();

    boolean setDefault(Long id);

}
