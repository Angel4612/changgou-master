package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SkuController {

    @Autowired
    private SkuService skuService;


    /**
     * 搜索
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map searchMap) {
        return skuService.search(searchMap);
    }

    /**
     * 导入数据
     */
    @GetMapping("/import")
    public Result search() {
        skuService.importSku();
        return new Result(true, StatusCode.OK,"导入数据到索引库中成功！");
    }
}
