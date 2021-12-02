package com.changgou.search.dao;

import com.changgou.goods.pojo.Sku;
import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 主要用于将数据导入到ES索引库中
 */
@Repository
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
