package com.atguigu.gulimall.pms.vo.detail;

import lombok.Data;

import java.util.List;

@Data
public class DetailAttrGroup {

    private Long id;
    private String name;//分组的名字
    private List<DetailBaseAttrVo> attrs;
}
