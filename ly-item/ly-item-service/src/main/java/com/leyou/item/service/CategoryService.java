package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryByParentId(Long id) {
        Category category = new Category();
        category.setParentId(id);//0
        //select * from tb_Category where parent_id=0
        return categoryMapper.select(category);
    }

    public List<Category> queryByBrandId(Long bid) {
        return  categoryMapper.queryByBrandId(bid);
    }

    public List<String> queryNamesByIds(List<Long> asList) {//74 75 76
        List<Category> categories = categoryMapper.selectByIdList(asList);

        List<String> names = new ArrayList<>();
        categories.forEach(s->{
            names.add(s.getName());
        });
        return  names;
    }
}
