package com.atguigu.gulimall.pms.vo;

import lombok.Data;

@Data
public class BaseAttrVo {

    /**
     * 属性id  名字  属性值
     */
    private Long attrId;

    private String attrName;

    private String[] valueSelected;
}
