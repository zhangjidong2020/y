package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    public CategoryService categoryService;
    @Autowired
    public BrandService brandService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        //开启分页助手
        PageHelper.startPage(page,rows);

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){

            criteria.andLike("title","%"+key+"%");
            //where   title like  %波导%
        }
       if(null!=saleable){
            criteria.andEqualTo("saleable",saleable);
            // and saleable=true

       }
        //直接查询 并转换分页条件的结果
        Page<Spu>  spuPage= (Page<Spu>)spuMapper.selectByExample(example);
        //获取查询结果
        List<Spu> spus = spuPage.getResult();


        //查询spu转化spubo
        List<SpuBo> spuBos=new ArrayList<>();

        spus.forEach(spu->{
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu,spuBo);
            //分类id,转化中文
            List<String>  names=categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
           //names 手机 手机通讯 手机
            spuBo.setCname(StringUtils.join(names,"/"));//手机/手机通讯/手机
            Brand brand= brandService.queryBrandById(spu.getBrandId());
            spuBo.setBname(brand.getName());

            spuBos.add(spuBo);
        });

        return  new PageResult<>(spuPage.getTotal(),new Long(spuPage.getPages()),spuBos);
    }

    public void saveGoods(SpuBo spuBo) {
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime( new Date());
        //保存spu,返回主键
        spuMapper.insertSelective(spuBo);
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        List<Sku> skus = spuBo.getSkus();
        saveSkus(spuBo,skus);
        //发送消息
        sendMessage(spuBo.getId(),"insert");

    }

    private void sendMessage(Long id, String type) {
        //发送消息
       try{
           amqpTemplate.convertAndSend("item."+type,id);
       }catch (Exception e){
           System.out.println("id"+id);
           e.printStackTrace();
       }
        //routing key      item.insert  123
        //routing key      item.update  123
    }

    private void saveSkus(SpuBo spuBo, List<Sku> skus) {
        skus.forEach(sku->{
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime( new Date());
            //保存sku
            skuMapper.insertSelective(sku);
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });
    }

    public SpuDetail querySpuDetailBySpuId(Long spuId) {

        return  spuDetailMapper.selectByPrimaryKey(spuId);
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        skuList.forEach(s->{
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());

        });

        return  skuList;
    }
   @Transactional
    public void updateGoods(SpuBo spuBo) {

        spuBo.setLastUpdateTime(new Date());

        spuMapper.updateByPrimaryKeySelective(spuBo);
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());//2
        List<Sku> skus = this.skuMapper.select(sku);//

        if(!CollectionUtils.isEmpty(skus)){

      /*      Stream<Sku> stream = skus.stream();
            Stream<Long> longStream = stream.map(Sku::getId);//
            List<Long> longs = longStream.collect(Collectors.toList());*/

            List<Long> longs = skus.stream().map(Sku::getId).collect(Collectors.toList());
            //2600242 2600248
            this.stockMapper.deleteByIdList(longs);
            //
            this.skuMapper.delete(sku);
            //delete from tb_sku where spu_id=2
        }

        saveSkus(spuBo,spuBo.getSkus());

       //发送消息
       sendMessage(spuBo.getId(),"update");
       //

    }

    public Spu querySpuById(Long id) {
        return  spuMapper.selectByPrimaryKey(id);
    }
}
