package com.qoder.mall.service;

import com.qoder.mall.vo.CategoryVO;

import java.util.List;

public interface ICategoryService {

    List<CategoryVO> getCategoryTree();
}
