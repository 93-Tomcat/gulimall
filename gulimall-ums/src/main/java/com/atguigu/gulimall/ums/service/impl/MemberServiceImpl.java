package com.atguigu.gulimall.ums.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.exception.EmailExistException;
import com.atguigu.gulimall.commons.exception.PhoneExistException;
import com.atguigu.gulimall.commons.exception.UsernameAndPasswordInvaildException;
import com.atguigu.gulimall.commons.exception.UsernameExistException;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import com.atguigu.gulimall.ums.vo.MemberLoginVo;
import com.atguigu.gulimall.ums.vo.MemberRegistVo;
import com.atguigu.gulimall.ums.vo.MemberRespVo;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.ums.dao.MemberDao;
import com.atguigu.gulimall.ums.entity.MemberEntity;
import com.atguigu.gulimall.ums.service.MemberService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberDao memberDao;

    @Autowired
    StringRedisTemplate redisTemplate;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void registerUser(String username, String password) {


        //密码加密存储
        //设置数据库的唯一约束
    }

    @Override
    public void registerUser(MemberRegistVo vo) {

        MemberEntity entity = new MemberEntity();

        //entity设置其他字段的默认值 生成的永远是不一样的
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String encode = encoder.encode(vo.getPassword());
        entity.setUsername(vo.getUsername());
        entity.setPassword(encode);
        entity.setEmail(vo.getEmail());
        entity.setMobile(vo.getPhone());

        Integer username = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username",vo.getUsername()));
        if (username >0){
            throw new UsernameExistException();
        }

        Integer email = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("email",vo.getEmail()));
        if (email >0){
            throw new EmailExistException();
        }

        Integer mobile = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile",vo.getPhone()));
        if (mobile >0){
            throw new PhoneExistException();
        }

        memberDao.insert(entity);
    }

    @Override
    public MemberRespVo login(MemberLoginVo vo) {

        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<MemberEntity>()
                .or().eq("username", vo.getLoginacct())
                .or().eq("mobile", vo.getLoginacct())
                .or().eq("email", vo.getLoginacct());

        MemberEntity one = memberDao.selectOne(wrapper);

        if(one == null){
            //登录失败
            throw new UsernameAndPasswordInvaildException();
        }

        //MD5
        boolean matches = new BCryptPasswordEncoder().matches(vo.getPassword(), one.getPassword());
        if (matches) {
            //登录成功
            //1、将用户的详细信息保存在redis中；
            String token = UUID.randomUUID().toString().replace("-", "");

            redisTemplate.opsForValue().set(Constant.LOGIN_USER_PREFIX+token, JSON.toJSONString(one),Constant.LOGIN_USER_TIMEOUT_MINUTES, TimeUnit.MINUTES);


            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            map.put("id",one.getId());
            //将我们在redis中的token做成jwt返回过去
            String jwt = GuliJwtUtils.buildJwt(map, null);

            MemberRespVo respVo = new MemberRespVo();
            BeanUtils.copyProperties(one,respVo);
            respVo.setToken(jwt);

            return respVo;

        }else {
            throw new UsernameAndPasswordInvaildException();
        }

    }


}