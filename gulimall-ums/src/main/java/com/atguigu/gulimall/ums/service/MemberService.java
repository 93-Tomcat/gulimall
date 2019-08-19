package com.atguigu.gulimall.ums.service;

import com.atguigu.gulimall.commons.exception.EmailExistException;
import com.atguigu.gulimall.commons.exception.PhoneExistException;
import com.atguigu.gulimall.commons.exception.UsernameExistException;
import com.atguigu.gulimall.ums.vo.MemberLoginVo;
import com.atguigu.gulimall.ums.vo.MemberRegistVo;
import com.atguigu.gulimall.ums.vo.MemberRespVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 会员
 *
 * @author 93丨
 * @email 17717080887_job@163.com
 * @date 2019-08-01 20:26:51
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 注册
     * @param username
     * @param password
     */
    void registerUser(String username, String password);

    void registerUser(MemberRegistVo vo)throws UsernameExistException, EmailExistException, PhoneExistException;

    MemberRespVo login(MemberLoginVo vo);
}

