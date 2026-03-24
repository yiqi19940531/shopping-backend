package com.qoder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qoder.mall.entity.Category;
import com.qoder.mall.mapper.CategoryMapper;
import com.qoder.mall.service.ICategoryService;
import com.qoder.mall.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSortOrder)
        );

        List<CategoryVO> voList = categories.stream().map(this::toVO).collect(Collectors.toList());

        Map<Long, List<CategoryVO>> groupByParent = voList.stream()
                .collect(Collectors.groupingBy(CategoryVO::getParentId));

        voList.forEach(vo -> vo.setChildren(groupByParent.getOrDefault(vo.getId(), new ArrayList<>())));

        return voList.stream()
                .filter(vo -> vo.getParentId() == 0L)
                .collect(Collectors.toList());
    }

    private CategoryVO toVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setParentId(category.getParentId());
        vo.setLevel(category.getLevel());
        vo.setSortOrder(category.getSortOrder());
        return vo;
    }
}
