package com.changgou.item.service;

public interface PageService {
    /**
     * 根据商品的ID 生成静态页
     */
    public void createPageHtml(Long spuId);
}
