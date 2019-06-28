package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> pageQuery(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //开启分页助手
        PageHelper.startPage(page,rows);

        Example example = new Example(Brand.class);
        if(StringUtils.isNotBlank(key)){
            Example.Criteria criteria = example.createCriteria();
            criteria.andLike("name","%"+key+"%");
            //where   name like  %波导%
        }
        //id
       if(StringUtils.isNoneBlank(sortBy)){
           //asc 升序 DESC 降序
           //order by id ASC
           example.setOrderByClause(sortBy+(desc ? " DESC":" ASC"));
       }
       //直接查询 并转换分页条件的结果
        Page<Brand> brandPage =( Page<Brand>) brandMapper.selectByExample(example);
       //select * from tb_brand where name like  %波导% order by id ASC limit 0,10



       //select count(*) from tb_brand where name like  %波导% order by id ASC limit 0,10
        return  new PageResult<>(brandPage.getTotal(),new Long(brandPage.getPages()),brandPage);
    }
    @Transactional
    public void addBrand(Brand brand, List<Long> cids) {

        brandMapper.insertSelective(brand);//id 1011
        //76 80
        cids.forEach(cid->{
                         //76,1011
                         //80,1011
             brandMapper.insertBrandCategory(cid,brand.getId());

        });
    }
    @Transactional
    public void uploadBrand(Brand brand, List<Long> cids) {

           this.brandMapper.updateByPrimaryKeySelective(brand);
           //
           this.brandMapper.deleteBrandCategory(brand.getId());


           cids.forEach(cid->{
            //76,1011
            //80,1011
            brandMapper.insertBrandCategory(cid,brand.getId());

           });

    }

    public Brand queryBrandById(Long brandId) {
       return  brandMapper.selectByPrimaryKey(brandId);
    }

    public List<Brand> queryBrandByCategory(Long cid) {

        return  brandMapper.queryBrandByCategory(cid);
    }
}
