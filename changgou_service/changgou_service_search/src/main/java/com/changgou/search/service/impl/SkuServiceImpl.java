package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;



    @Override
    public void importSku() {
        // 调用changgou-service-goods微服务
        Result<List<Sku>> skuListResult = skuFeign.findByStatus("1");
        //将数据转成search.Sku
        List<SkuInfo> skuInfos=  JSON.parseArray(JSON.toJSONString(skuListResult.getData()),SkuInfo.class);
        for(SkuInfo skuInfo:skuInfos){
            Map<String, Object> specMap= JSON.parseObject(skuInfo.getSpec()) ;
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfos);
    }

    @Override
    public Map search(Map<String, String> searchMap) {
        // 获取所有的关键字
        String keywords = searchMap.get("keywords");
        // 如果为空, 赋值一个默认值
        if (StringUtils.isEmpty(keywords)) {
            keywords = "";
        }
        // 创建查询对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        // 设置分组条件, 商品分类
        nativeSearchQueryBuilder.addAggregation(
                AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(50));

        // 设置查询条件
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
        nativeSearchQueryBuilder.withPageable(PageRequest.of(1, 10));
        // 构建查询对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        // 执行查询
        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(query, SkuInfo.class);

        // 获取分组结果
        StringTerms stringTerms = (StringTerms) skuPage.getAggregation("skuCategorygroup");
        ArrayList<String> categoryList = new ArrayList<>();
        if (Objects.nonNull(stringTerms)) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                String keyAsString = bucket.getKeyAsString();
                categoryList.add(keyAsString);
            }
        }

        // 返回结果
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", skuPage.getContent());
        resultMap.put("total", skuPage.getTotalElements());
        resultMap.put("totalPages", skuPage.getTotalPages());
        resultMap.put("categoryList", categoryList);
        return resultMap;
    }
}
