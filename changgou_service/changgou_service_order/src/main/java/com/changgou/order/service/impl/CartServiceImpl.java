package com.changgou.order.service.impl;

import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 加入购物车
     * @param num       商品数量
     * @param id        商品ID
     * @param username  用户
     */
    @Override
    public void add(Integer num, Long id, String username) {

        if (num <= 0) {
            return;
        }

        // 查询SKU的Result
        Result<Sku> skuResult = skuFeign.findById(id);
        // 获取Result中的sku
        if (skuResult != null && skuResult.isFlag()) { // 如果有skuResult, 并且是成功的
            // 获取sku
            Sku sku = skuResult.getData();
            // 通过sku中的spuId获取Spu
            // 获取spu
            Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
            // 将sku转换成OrderItem
            OrderItem orderItem = sku2OrderItem(sku, spuResult.getData(), num);
            // 将OrderItem数据, 存到Redis中(namespace:用户名  key:商品id  value:OrderItem)
            redisTemplate.boundHashOps("Cart_" + username).put(id, orderItem);
        }
    }


    /**
     * 查询用户的购物车数据
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> list(String username) {
        List<OrderItem> orderItems = redisTemplate.boundHashOps("Cart_" + username).values();
        return orderItems;
    }


    /**
     * sku转换成OrderItem
     */
    private OrderItem sku2OrderItem(Sku sku, Spu spu, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setSpuId(sku.getSpuId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(num*orderItem.getPrice());       //单价*数量
        orderItem.setPayMoney(num*orderItem.getPrice());    //实付金额
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(sku.getWeight()*num);           //重量=单个重量*数量

        //分类ID设置
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        return orderItem;
    }

}
