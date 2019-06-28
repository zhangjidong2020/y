package com.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi {

    @GetMapping("category/names")
    public List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);
    //http://127.0.0.1:9081/category/names
}
