package com.leyou.search.service;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecParam;
import com.leyou.search.clients.BrandClient;
import com.leyou.search.clients.CategoryClient;
import com.leyou.search.clients.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.utils.SearchRequest;
import com.leyou.search.utils.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MultiMatchQuery;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
@Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecClient specClient;

    public SearchResult page(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if(!StringUtils.isNotBlank(key)){
             return  null;
        }
        //自定义查询
       NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //调用构建方法，返回一个查询条件
       QueryBuilder query=buildBasicQueryWithFilter(searchRequest);

       queryBuilder.withQuery(query);

       queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));
       //聚合
       String categoryAggName="category";
       String brandAggName="brand";

       queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
       queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

       List<Category> categories=new ArrayList<>();
       List<Brand> brands=new ArrayList<>();

      //执行查询
       AggregatedPage<Goods> goodsPage= (AggregatedPage<Goods>)goodsRepository.search(queryBuilder.build());
      //获取分类数据
       LongTerms  categoryTerms=(LongTerms) goodsPage.getAggregation(categoryAggName);//category

       List<LongTerms.Bucket> buckets = categoryTerms.getBuckets();

       List<Long> cids=new ArrayList<>();
       buckets.forEach(bucket ->cids.add(bucket.getKeyAsNumber().longValue()) );
       //cids 76 ,79
       List<String> names = categoryClient.queryNamesByIds(cids);

       for (int i=0;i<cids.size();i++){
           Category category = new Category();
           category.setId(cids.get(i));//76
           category.setName(names.get(i));//手机
           categories.add(category);
       }


       //获取品牌数据
       List<Long> brandIds=new ArrayList<>();

       LongTerms  brandTerms=(LongTerms) goodsPage.getAggregation(brandAggName);//brand
       brandTerms.getBuckets().forEach(bucket -> brandIds.add(bucket.getKeyAsNumber().longValue()));
       //brandIds 15127 8557
       brandIds.forEach(brandId->{
           Brand brand = brandClient.queryBrandById(brandId);
           brands.add(brand);
       });

       List<Map<String,Object>> specs=null;
       //分类唯一才展示规格参数
       if(categories.size()==1){
           specs=  getSpecs(categories.get(0).getId(),query);

       }
     // public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands,List<Map<String,Object>> specs) {
       return  new SearchResult(goodsPage.getTotalElements(),new Long(goodsPage.getTotalPages()),goodsPage.getContent(),categories,brands,specs);

   }

    private List<Map<String, Object>> getSpecs(Long  id, QueryBuilder query) {
        List<Map<String,Object>> specList = new ArrayList<>();

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //先根据查询条件
        queryBuilder.withQuery(query);
        List<SpecParam> searchingSpecParams = specClient.querySpecParam(null, id, true, null);//76
       // 7	76	3	操作系统	0		1	1
       // 8	76	4	CPU品牌	0		1	1
        searchingSpecParams.forEach(specParam -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));

        });
        AggregatedPage<Goods>  page=(AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());

        searchingSpecParams.forEach(specParam -> {
            String name=  specParam.getName();//操作系统

           StringTerms stringTerms=(StringTerms) page.getAggregation(name);

           List<String>  values=new ArrayList<>();

            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
            buckets.forEach(bucket -> values.add(bucket.getKeyAsString()));//Android IOs


            Map<String,Object> specMap=new HashMap<>();
            specMap.put("k",name);//k  操作系统
            specMap.put("options",values);//options  Android IOs

            specList.add(specMap);

        });
        return specList;
    }

    private QueryBuilder buildBasicQueryWithFilter(SearchRequest searchRequest) {
       //创建基本的bool查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        //基本查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));

        //过滤条件
        BoolQueryBuilder filterQueryBuilder=  QueryBuilders.boolQuery();
       //获得过滤条件的数据
        Map<String, String> filter = searchRequest.getFilter();
        for(Map.Entry<String,String> entry:filter.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            //因为规格参数保存时不做分词，因此其名称会自动带上一个.keyword后缀：
            if(key!="cd3"&&key!="brandId"){
                     key="specs."+key+".keyword";
                     // specs.cpu频率.keyword
                     filterQueryBuilder.must(QueryBuilders.termQuery(key,value));//specs.cpu频率.keyword   1.5-2.0GHz
                                                                                 //specs.后置摄像头.keyword   0-400
            }

        }
        queryBuilder.filter(filterQueryBuilder);
        return  queryBuilder;

    }
}
