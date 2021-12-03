package com.changgou.controller;


import com.changgou.entity.Page;
import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller  // 要跳转页面所以是Controller
@RequestMapping(value = "/search")
@CrossOrigin
public class SkuController {

    @Autowired
    private SkuFeign skuFeign;

    /**
     * 实现页面搜索
     */
    @GetMapping("/list")
    public String searche(@RequestParam(required = false)Map<String, String> searchMap, Model model) {
        // 调用搜索微服务
        Map resultMap = skuFeign.search(searchMap);
        // 搜索数据结果
        model.addAttribute("result", resultMap);
        // 搜索条件
        model.addAttribute("searchMap", searchMap);
        // 请求地址
        String url = url(searchMap);
        model.addAttribute("url", url);

        Page<SkuInfo> page = new Page<>(
                Long.parseLong(resultMap.get("totalPages").toString()),
                Integer.parseInt(resultMap.get("pageNum").toString()),
                Integer.parseInt(resultMap.get("pageSize").toString())
        );

        model.addAttribute("page", page);


        return "search";
    }


    /**
     * URL组装和处理
     */
    public String url(Map<String, String> searchMap) {
        // URL地址
        String url = "/search/list";
        if (searchMap != null && searchMap.size() > 0) {
            url += "?";
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();

                // 跳过分页
                if(key.equals("pageNum")){
                    continue;
                }
                //如果是排序 则 跳过 拼接排序的地址 因为有数据
                if(key.equals("sortField") || key.equals("sortRule")){
                    continue;
                }

                url += key + "=" + entry.getValue() + "&";
            }
        }

        return url.endsWith("&") ? url.substring(0, url.length() - 1) : url;
    }

}
