package com.qoder.mall.controller.admin;

import com.qoder.mall.common.exception.BusinessException;
import com.qoder.mall.common.result.Result;
import com.qoder.mall.entity.Category;
import com.qoder.mall.mapper.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Tag(name = "后台-分类管理", description = "管理端分类CRUD接口")
public class AdminCategoryController {

    private final CategoryMapper categoryMapper;

    @Data
    public static class CategoryRequest {
        @NotBlank(message = "分类名称不能为空")
        private String name;
        private Long parentId = 0L;
        private Integer level = 1;
        private Integer sortOrder = 0;
    }

    @PostMapping
    @Operation(summary = "新增分类")
    public Result<Category> create(@RequestBody CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setLevel(request.getLevel());
        category.setSortOrder(request.getSortOrder());
        category.setStatus(1);
        categoryMapper.insert(category);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新分类")
    public Result<Void> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setLevel(request.getLevel());
        category.setSortOrder(request.getSortOrder());
        categoryMapper.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result<Void> delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
