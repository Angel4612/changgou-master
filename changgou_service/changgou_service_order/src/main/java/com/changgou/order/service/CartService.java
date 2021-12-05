package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

public interface CartService {

    /***
     * 添加商品到购物车
     *
     * @param num       商品数量
     * @param id        商品ID
     * @param username  用户
     */
    void add(Integer num, Long id, String username);


    /**
     * 查询用户的购物车数据
     */
    List<OrderItem> list(String username);
}
