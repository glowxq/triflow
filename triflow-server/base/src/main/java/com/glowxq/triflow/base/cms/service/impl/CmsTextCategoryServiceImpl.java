package com.glowxq.triflow.base.cms.service.impl;

import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.cms.entity.CmsTextCategory;
import com.glowxq.triflow.base.cms.mapper.CmsTextCategoryMapper;
import com.glowxq.triflow.base.cms.mapper.CmsTextMapper;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryCreateDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryQueryDTO;
import com.glowxq.triflow.base.cms.pojo.dto.CmsTextCategoryUpdateDTO;
import com.glowxq.triflow.base.cms.pojo.vo.CmsTextCategoryVO;
import com.glowxq.triflow.base.cms.service.CmsTextCategoryService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文本分类 Service 实现类
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CmsTextCategoryServiceImpl extends ServiceImpl<CmsTextCategoryMapper, CmsTextCategory>
        implements CmsTextCategoryService {

    private final CmsTextMapper cmsTextMapper;

    @Override
    public List<CmsTextCategoryVO> getTree(CmsTextCategoryQueryDTO query) {
        // 获取所有分类
        List<CmsTextCategory> allCategories = mapper.selectByQuery(query);
        List<CmsTextCategoryVO> allVOs = MapStructUtils.convert(allCategories, CmsTextCategoryVO.class);

        // 构建树形结构
        return buildTree(allVOs, 0L);
    }

    @Override
    public List<CmsTextCategoryVO> getList(CmsTextCategoryQueryDTO query) {
        List<CmsTextCategory> categories = mapper.selectByQuery(query);
        return MapStructUtils.convert(categories, CmsTextCategoryVO.class);
    }

    @Override
    public CmsTextCategoryVO getDetail(Long id) {
        CmsTextCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return MapStructUtils.convert(category, CmsTextCategoryVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CmsTextCategoryCreateDTO dto) {
        // 校验分类标识唯一性
        if (existsByCategoryKey(dto.getCategoryKey(), null)) {
            throw new BusinessException("分类标识已存在");
        }

        // 校验父分类是否存在
        if (dto.getPid() != null && dto.getPid() > 0) {
            CmsTextCategory parent = getById(dto.getPid());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
        }

        CmsTextCategory category = MapStructUtils.convert(dto, CmsTextCategory.class);
        if (category.getPid() == null) {
            category.setPid(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }

        save(category);
        log.info("创建文本分类成功: id={}, categoryKey={}", category.getId(), category.getCategoryKey());
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(CmsTextCategoryUpdateDTO dto) {
        CmsTextCategory existingCategory = getById(dto.getId());
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }

        // 校验分类标识唯一性
        if (dto.getCategoryKey() != null && existsByCategoryKey(dto.getCategoryKey(), dto.getId())) {
            throw new BusinessException("分类标识已存在");
        }

        // 校验父分类（不能设置为自己或自己的子分类）
        if (dto.getPid() != null && dto.getPid() > 0) {
            if (dto.getPid().equals(dto.getId())) {
                throw new BusinessException("父分类不能是自己");
            }
            CmsTextCategory parent = getById(dto.getPid());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
            // 检查是否形成循环
            if (isDescendant(dto.getPid(), dto.getId())) {
                throw new BusinessException("父分类不能是自己的子分类");
            }
        }

        CmsTextCategory category = MapStructUtils.convert(dto, CmsTextCategory.class);
        boolean result = updateById(category);
        log.info("更新文本分类成功: id={}", dto.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        CmsTextCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 检查是否有子分类
        if (hasChildren(id)) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }

        // 检查是否有文本内容
        if (hasTexts(id)) {
            throw new BusinessException("该分类下存在文本内容，无法删除");
        }

        boolean result = removeById(id);
        log.info("删除文本分类成功: id={}", id);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
        return true;
    }

    @Override
    public boolean existsByCategoryKey(String categoryKey, Long excludeId) {
        return mapper.existsByCategoryKey(categoryKey, excludeId);
    }

    @Override
    public boolean hasChildren(Long id) {
        List<CmsTextCategory> children = mapper.selectByPid(id);
        return children != null && !children.isEmpty();
    }

    @Override
    public boolean hasTexts(Long id) {
        return !cmsTextMapper.selectByCategoryId(id).isEmpty();
    }

    @Override
    public CmsTextCategory getByCategoryKey(String categoryKey) {
        return mapper.selectByCategoryKey(categoryKey);
    }

    @Override
    public List<CmsTextCategoryVO> listAllEnabled() {
        List<CmsTextCategory> categories = mapper.selectAllEnabled();
        return MapStructUtils.convert(categories, CmsTextCategoryVO.class);
    }

    /**
     * 构建树形结构
     */
    private List<CmsTextCategoryVO> buildTree(List<CmsTextCategoryVO> allVOs, Long pid) {
        List<CmsTextCategoryVO> result = new ArrayList<>();

        // 按父ID分组
        Map<Long, List<CmsTextCategoryVO>> groupByPid = allVOs.stream()
                .collect(Collectors.groupingBy(vo -> vo.getPid() == null ? 0L : vo.getPid()));

        // 获取指定父ID的子节点
        List<CmsTextCategoryVO> children = groupByPid.get(pid);
        if (children != null) {
            for (CmsTextCategoryVO child : children) {
                // 递归构建子树
                child.setChildren(buildTree(allVOs, child.getId()));
                result.add(child);
            }
        }

        return result;
    }

    /**
     * 检查是否是某个分类的后代
     */
    private boolean isDescendant(Long checkId, Long ancestorId) {
        CmsTextCategory check = getById(checkId);
        if (check == null) {
            return false;
        }
        if (check.getPid() == null || check.getPid() == 0) {
            return false;
        }
        if (check.getPid().equals(ancestorId)) {
            return true;
        }
        return isDescendant(check.getPid(), ancestorId);
    }

}
