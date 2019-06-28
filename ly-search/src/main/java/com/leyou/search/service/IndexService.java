package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.search.clients.CategoryClient;
import com.leyou.search.clients.GoodsClient;
import com.leyou.search.clients.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecClient specClient;
    @Autowired
    private GoodsRepository goodsRepository;

    //160	魅族（MEIZU） 魅蓝E3 全面屏手机 全网通4G手机 	【128G金色少量到货】预售30天发货 骁龙8核处理器，6G大内存，索尼双摄，低温快充	74	75	76	12669	1	1	2018-04-21 16:00:44	2018-04-21 16:00:44
    public Goods buildGoods(SpuBo spuBo) {
         Goods goods = new Goods();

        //对应拷贝赋值相同的属性
        BeanUtils.copyProperties(spuBo,goods);
        //74	75	76
        List<String> names = categoryClient.queryNamesByIds(Arrays.asList(spuBo.getCid1(), spuBo.getCid2(), spuBo.getCid3()));
        //魅族（MEIZU) 手机 手机通讯 手机
        String all=spuBo.getTitle()+" "+ StringUtils.join(names," ");
        goods.setAll(all);
        //spuid查询sku
        // spuBo.getId()
        List<Sku> skus = goodsClient.querySkuBySpuId(spuBo.getId());

        List<Map<String,Object>>  skuMapList=new ArrayList<>();
        List<Long> prices= new ArrayList<>();

        skus.forEach(sku -> {
            prices.add(sku.getPrice()) ;
            Map<String,Object>  skuMap=new HashMap();
            skuMap.put("id",sku.getId());//id 2868393
            skuMap.put("title",sku.getTitle());//title 三星 Galaxy C5（SM-C5000）4GB+32GB 枫叶金 移动联通电信4G手机 双卡双待
            skuMap.put("price",sku.getPrice());
            skuMap.put("image",StringUtils.isBlank(sku.getImages())?"":sku.getImages().split(",")[0]);

            skuMapList.add(skuMap);
        });

        goods.setSkus(JsonUtils.serialize(skuMapList));
        goods.setPrice(prices);

        //获取可搜索的规格参数spubo.getCid3
        Long cid3=spuBo.getCid3();
        List<SpecParam> specParamList = specClient.querySpecParam(null, cid3, true, null);

        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spuBo.getId());
      //{"1":"其它1","2":"1G9青春版（全网通版）","3":2016,"5":143,"6":"其它1","7":"And1roid","8":"骁龙（1Snapdragon)","9":"骁龙6171（msm8952）","10":"八核1","11":1.5,"14":5.2,"15":"1920*1080(FHD)","16":800,"17":1300,"18":"30001"}
        Map<Long, Object> genericMap = JsonUtils.nativeRead(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>(){});
        //1 "其它1"
        //2 "1G9青春版（全网通版）"
        Map<Long, Object> specialMap = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, Object>>(){});

        Map<String,Object> specs=new HashMap<>();

        specParamList.forEach(specParam -> {

                Long id = specParam.getId();//1
                String name = specParam.getName();//品牌
                Object value=null;
                if(specParam.getGeneric()){
                    value=genericMap.get(id);//其它1

                            if(null!=value&&specParam.getNumeric()){
                                value=this.chooseSegment(value.toString(),specParam);//4.5-3.1英寸


                            }

            }else{

                value=specialMap.get(id);
            }

            if(value==null){
                value="其他";
            }
            //操作系统  Android
            specs.put(name,value);
        });
        goods.setSpecs(specs);
        return  goods;
    }



    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


    public void createIndex(Long id) {
        Spu spu = goodsClient.querySpuById(id);


        SpuBo spuBo = new SpuBo();

        BeanUtils.copyProperties(spu,spuBo);

        Goods goods = this.buildGoods(spuBo);

        this.goodsRepository.save(goods);


    }

    public void deleteIndex(Long id) {
        goodsRepository.deleteById(id);
    }
}
