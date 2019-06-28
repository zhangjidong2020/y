package com.leyou.search.utils;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.search.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchResult extends PageResult<Goods>{

    private List<Category> categories;

    private List<Brand> brands;

    private  List<Map<String,Object>> specs;

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands,List<Map<String,Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs=specs;

    }
}