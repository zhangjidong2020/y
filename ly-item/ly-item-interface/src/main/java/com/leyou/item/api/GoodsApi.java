package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuByPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",required = false) Boolean  saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "10") Integer rows);
    //  http://item-service/spu/page
    //  http://127.0.0.1:9081/spu/page

    @GetMapping("sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id") Long spuId);


    @GetMapping("spu/detail/{spuId}")
    public  SpuDetail querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("spu/{id}")
    public  Spu querySpuById(@PathVariable("id") Long id);
    // http://127.0.0.1:9081/spu/113.html
}
