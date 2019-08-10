/**
 * Copyright (c) 2016-2019 谷粒开源 All rights reserved.
 *
 * https://www.guli.cloud
 *
 * 版权所有，侵权必究！
 */

package com.atguigu.gulimall.commons.bean;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类
 *
 * @author Mark sunlightcs@gmail.com
 */
@ApiModel
@Data
public class PageVo implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 总记录数
	 */
	private Long totalCount;
	/**
	 * 每页记录数
	 */
	private Long pageSize;
	/**
	 * 总页数
	 */
	private Long totalPage;
	/**
	 * 当前页数
	 */
	private Long currPage;
	/**
	 * 列表数据
	 */
	private List<?> list;
	
	/**
	 * 分页
	 * @param list        列表数据
	 * @param totalCount  总记录数
	 * @param pageSize    每页记录数
	 * @param currPage    当前页数
	 */
	public PageVo(List<?> list, Long totalCount, Long pageSize, Long currPage) {
		this.list = list;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.currPage = currPage;
		this.totalPage = (long)Math.ceil((double)totalCount/pageSize);
	}

	/**
	 * 分页
	 */
	public PageVo(IPage<?> page) {
		this.list = page.getRecords();
		this.totalCount = (Long)page.getTotal();
		this.pageSize = (Long)page.getSize();
		this.currPage = (Long)page.getCurrent();
		this.totalPage = (Long)page.getPages();
	}


	
}
