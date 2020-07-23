package com.wen.commons.web;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页、排序信息
 *
 * @author denis.huang
 * @since 2017年2月15日
 */
public class PageParams implements Serializable {
    private static final long serialVersionUID = -2960398574777651921L;

    /** 当前页 */
    @Min(1)
    private int pageNum = 1;

    /** 每页记录数 */
    @Min(1)
    @Max(100)
    private int pageSize = 10;

    /** 总记录数 */
    private long total = 0;

    /** 总页数 */
    private int pages = 0;

    /** 排序字段 */
    private String sortName = "";

    /** 排序方式，默认asc */
    private String sortType = "";

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
