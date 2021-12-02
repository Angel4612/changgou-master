package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
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
                AggregationBuilders.terms("skuCategoryGroup").field("categoryName").size(50));

        // 设置分组条件, 商品品牌
        nativeSearchQueryBuilder.addAggregation(
                AggregationBuilders.terms("skuBrandGroup").field("brandName").size(50));

        // 设置分组条件, 商品规格
        nativeSearchQueryBuilder.addAggregation(
                AggregationBuilders.terms("skuSpecGroup").field("spec.keyword").size(100));

        // 设置查询条件
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));
        nativeSearchQueryBuilder.withPageable(PageRequest.of(1, 10));
        // 构建查询对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        // 执行查询
        AggregatedPage<SkuInfo> skuPage = elasticsearchTemplate.queryForPage(query, SkuInfo.class);

        // 获取分组结果
        StringTerms stringTermsCategory = (StringTerms) skuPage.getAggregation("skuCategoryGroup");

        List<String> categoryList = getStringList(stringTermsCategory);

        // 获取分组结果 品牌
        StringTerms stringTermsBrand = (StringTerms) skuPage.getAggregation("skuBrandGroup");

        List<String> brandList = getStringList(stringTermsBrand);

        // 获取分组结果 商品规格数据
        StringTerms stringTermsSpec = (StringTerms) skuPage.getAggregation("skuSpecGroup");

        Map<String, Set<String>> SpecList = getStringSetMap(stringTermsSpec);


        // 返回结果
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", skuPage.getContent());
        resultMap.put("total", skuPage.getTotalElements());
        resultMap.put("totalPages", skuPage.getTotalPages());
        resultMap.put("categoryList", categoryList);
        resultMap.put("brandList", brandList);
        resultMap.put("SpecList", SpecList);
        return resultMap;
    }

    private Map<String, Set<String>> getStringSetMap(StringTerms stringTermsSpec) {
        // 创建Map集合
        HashMap<String, Set<String>> specMap = new HashMap<>();
        // 创建Set集合
        HashSet<String> specList = new HashSet<>();
        // 先将信息存入set集合中, 存入的都是这样的字符串:
        if (stringTermsSpec != null) {
            for (StringTerms.Bucket bucket : stringTermsSpec.getBuckets()) {
                specList.add(bucket.getKeyAsString());
            }
        }

        // 遍历specList, 准备将里面的数据封装成Map, 然后返回
        for (String specJson : specList) {
            // 将specJson字符串, 转换成Map集合
            Map<String, String> map = JSON.parseObject(specJson, Map.class);
            // 遍历map集合
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey(); // 规格名字
                String value = entry.getValue(); // 规格选项
                // 获取当前规格名字对应的规格数据
                Set<String> specValues = specMap.get(key);
                if (specValues == null) {
                    specValues = new HashSet<String>();
                }
                // 将当前规格加入到集合中
                specValues.add(value);
                // 将数据存储到specMap中
                specMap.put(key, specValues);
            }
        }
        //
        return specMap;
    }

    /**
     * 根据StringTerms 获取集合
     * @param stringTerms
     * @return
     */
    private List<String> getStringList(StringTerms stringTerms) {
        ArrayList<String> list = new ArrayList<>();
        if (stringTerms != null) {
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                list.add(bucket.getKeyAsString());
            }
        }
        return list;
    }

}
