package com.changgou.order.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 加入购物车
     */
    @RequestMapping("/add")
    public Result add(Integer num, Long id) {
        // 用户名
        String username = "szitheima"; // 写死了~

        // 将商品加入购物车
        cartService.add(num, id, username);
        return new Result(true, StatusCode.OK,"加入购物车成功！");
    }


    /**
     * 查询用户的购物车列表
     */
    @GetMapping("/list")
    public Result<List<OrderItem>> list() {
        //用户名
        String username="szitheima";
        List<OrderItem> orderItems = cartService.list(username);
        return new Result(true,StatusCode.OK,"购物车列表查询成功！",orderItems);
    }

}
