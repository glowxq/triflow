package com.glowxq.triflow.base.cms.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.cms.entity.CmsText;
import com.glowxq.triflow.base.cms.entity.CmsTextCategory;
import com.glowxq.triflow.base.cms.mapper.CmsTextCategoryMapper;
import com.glowxq.triflow.base.cms.mapper.CmsTextMapper;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCreateDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextQueryDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextUpdateDTO;
import com.glowxq.triflow.base.cms.pojo.vo.CmsTextVO;
import com.glowxq.triflow.base.cms.service.CmsTextService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文本内容 Service 实现类
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CmsTextServiceImpl extends ServiceImpl<CmsTextMapper, CmsText>
        implements CmsTextService {

    private final CmsTextCategoryMapper cmsTextCategoryMapper;

    @Override
    public Page<CmsTextVO> paginate(CmsTextQueryDTO query) {
        QueryWrapper qw = mapper.buildQueryWrapper(query);
        Page<CmsText> page = mapper.paginate(query.buildPage(), qw);
        return new Page<>(convertToVOList(page.getRecords()), page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    @Override
    public List<CmsTextVO> getList(CmsTextQueryDTO query) {
        List<CmsText> texts = mapper.selectByQuery(query);
        return convertToVOList(texts);
    }

    @Override
    public CmsTextVO getDetail(Long id) {
        CmsText text = getById(id);
        if (text == null) {
            throw new BusinessException("文本内容不存在");
        }
        return convertToVO(text);
    }

    @Override
    public CmsTextVO getByTextKey(String textKey) {
        CmsText text = mapper.selectByTextKey(textKey);
        if (text == null) {
            return null;
        }
        return convertToVO(text);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CmsTextCreateDTO dto) {
        // 校验文本标识唯一性
        if (existsByTextKey(dto.getTextKey(), null)) {
            throw new BusinessException("文本标识已存在");
        }

        // 校验分类是否存在
        if (dto.getCategoryId() != null) {
            CmsTextCategory category = cmsTextCategoryMapper.selectOneById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException("分类不存在");
            }
        }

        CmsText text = MapStructUtils.convert(dto, CmsText.class);

        // 设置默认值
        if (text.getTextType() == null) {
            text.setTextType("article");
        }
        if (text.getContentType() == null) {
            text.setContentType("html");
        }
        if (text.getSort() == null) {
            text.setSort(0);
        }
        if (text.getTop() == null) {
            text.setTop(0);
        }
        if (text.getRecommend() == null) {
            text.setRecommend(0);
        }
        if (text.getStatus() == null) {
            text.setStatus(0);
        }
        if (text.getViewCount() == null) {
            text.setViewCount(0);
        }
        if (text.getLikeCount() == null) {
            text.setLikeCount(0);
        }
        if (text.getVersion() == null) {
            text.setVersion(1);
        }

        save(text);
        log.info("创建文本内容成功: id={}, textKey={}", text.getId(), text.getTextKey());
        return text.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(CmsTextUpdateDTO dto) {
        CmsText existingText = getById(dto.getId());
        if (existingText == null) {
            throw new BusinessException("文本内容不存在");
        }

        // 校验文本标识唯一性
        if (dto.getTextKey() != null && existsByTextKey(dto.getTextKey(), dto.getId())) {
            throw new BusinessException("文本标识已存在");
        }

        // 校验分类是否存在
        if (dto.getCategoryId() != null) {
            CmsTextCategory category = cmsTextCategoryMapper.selectOneById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException("分类不存在");
            }
        }

        CmsText text = MapStructUtils.convert(dto, CmsText.class);
        // 版本号递增
        text.setVersion(existingText.getVersion() + 1);

        boolean result = updateById(text);
        log.info("更新文本内容成功: id={}", dto.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        CmsText text = getById(id);
        if (text == null) {
            throw new BusinessException("文本内容不存在");
        }

        boolean result = removeById(id);
        log.info("删除文本内容成功: id={}", id);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<Long> ids) {
        boolean result = removeByIds(ids);
        log.info("批量删除文本内容成功: ids={}", ids);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publish(Long id) {
        CmsText text = getById(id);
        if (text == null) {
            throw new BusinessException("文本内容不存在");
        }

        CmsText update = new CmsText();
        update.setId(id);
        update.setStatus(1);
        update.setPublishTime(LocalDateTime.now());

        boolean result = updateById(update);
        log.info("发布文本内容成功: id={}", id);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unpublish(Long id) {
        CmsText text = getById(id);
        if (text == null) {
            throw new BusinessException("文本内容不存在");
        }

        CmsText update = new CmsText();
        update.setId(id);
        update.setStatus(0);

        boolean result = updateById(update);
        log.info("下架文本内容成功: id={}", id);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setTop(Long id, Integer top) {
        CmsText text = getById(id);
        if (text == null) {
            throw new BusinessException("文本内容不存在");
        }

        CmsText update = new CmsText();
        update.setId(id);
        update.setTop(top);

        boolean result = updateById(update);
        log.info("设置文本置顶状态成功: id={}, top={}", id, top);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setRecommend(Long id, Integer recommend) {
        CmsText text = getById(id);
        if (text == null) {
            throw new BusinessException("文本内容不存在");
        }

        CmsText update = new CmsText();
        update.setId(id);
        update.setRecommend(recommend);

        boolean result = updateById(update);
        log.info("设置文本推荐状态成功: id={}, recommend={}", id, recommend);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementViewCount(Long id) {
        int rows = mapper.incrementViewCount(id);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementLikeCount(Long id) {
        CmsText text = getById(id);
        if (text == null) {
            return false;
        }

        CmsText update = new CmsText();
        update.setId(id);
        update.setLikeCount(text.getLikeCount() == null ? 1 : text.getLikeCount() + 1);

        return updateById(update);
    }

    @Override
    public boolean existsByTextKey(String textKey, Long excludeId) {
        return mapper.existsByTextKey(textKey, excludeId);
    }

    /**
     * 转换为VO并填充分类名称
     */
    private CmsTextVO convertToVO(CmsText text) {
        CmsTextVO vo = MapStructUtils.convert(text, CmsTextVO.class);
        if (text.getCategoryId() != null) {
            CmsTextCategory category = cmsTextCategoryMapper.selectOneById(text.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            }
        }
        return vo;
    }

    /**
     * 批量转换为VO
     */
    private List<CmsTextVO> convertToVOList(List<CmsText> texts) {
        return texts.stream().map(this::convertToVO).toList();
    }

}
