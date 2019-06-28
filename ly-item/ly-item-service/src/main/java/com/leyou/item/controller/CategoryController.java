package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public ResponseEntity<List<Category>>  queryByParentId(@RequestParam("pid") Long id){
        List<Category> categories=categoryService.queryByParentId(id);

       // List a=new ArrayList();

       //[]
       if(categories!=null&&categories.size()>0){
               return  ResponseEntity.ok(categories);
       }
       return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("bid/{bid}")
    public  ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid){
            List<Category> categories= categoryService.queryByBrandId(bid);
            return  ResponseEntity.ok(categories);
            //return  ResponseEntity.status(HttpStatus.ok).build(categories);
   }

    /**
     * 分类id集合 返回集合名称
     */
    @GetMapping("names")
    public  ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids){

        List<String> list= categoryService.queryNamesByIds(ids);
        if(list!=null&&list.size()>0){
            return  ResponseEntity.ok(list);
        }
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
