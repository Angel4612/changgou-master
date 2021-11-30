package com.changgou.entity;

import lombok.Data;

import java.util.List;

/**
 * 分页结果类
 */
@Data
public class PageResult<T> {

    private Long total;//总记录数
    private Long size; // 总页数
    private Long current; // 当前页
    private Boolean lastPage; // 是否为最后一页
    private List<T> rows;//记录
}
