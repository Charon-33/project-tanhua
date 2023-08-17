package com.wsj.server.service.impl;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.wsj.dubbo.api.UserApi;
import com.wsj.domain.User;
import com.wsj.server.exception.BusinessException;
import com.wsj.server.service.UserService;
import com.wsj.template.HuanXinTemplate;
import com.wsj.utils.Constants;
import com.wsj.utils.JwtUtils;
import com.wsj.vo.ErrorResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    //    @Autowired
//    private MailTemplate mailTemplate;
    @DubboReference
    private UserApi userApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;


    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void sendCheckCode(String phone) {
        //1、随机生成6位数字
//        String code = RandomStringUtils.randomNumeric(4);
        String code = "123456";
        //2、调用template对象，发送手机短信
//        mailTemplate.sendMail(mail,code,"注册验证码");
        //3、将验证码存入到redis
        redisTemplate.opsForValue().set("CHECK_CODE" + phone, code, Duration.ofMinutes(3));
    }

    @Override
    public Map<String, Object> loginVerification(String phone, String code) {
        //1、从redis中获取下发的验证码
        String redisCode = redisTemplate.opsForValue().get("CHECK_CODE" + phone);

        //2、对验证码进行校验（验证码是否存在，是否和输入的验证码一致）
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            //验证码无效
            throw new BusinessException(ErrorResult.loginError());
        }

        //3、删除redis中的验证码
        redisTemplate.delete("CHECK_CODE" + phone);

        //4、通过手机号码查询用户
        User user = userApi.findByMobile(phone);
        boolean isNew = false;

        //5、如果用户不存在，创建用户保存到数据库中
        if (user == null) {
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long userId = userApi.save(user);
            user.setId(userId);
            isNew = true;
            //注册环信用户
            String hxUser = "hx" + user.getId();
            Boolean create = huanXinTemplate.createUser(hxUser, Constants.INIT_PASSWORD);
            if(create){
                user.setHxUser(hxUser);
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.update(user);
            }
        }

        //6、通过JWT生成token(存入id和手机号码)
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", user.getId());
        tokenMap.put("mobile", user.getMobile());
        String token = JwtUtils.getToken(tokenMap);

        //7、构造返回值
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("token", token);
        retMap.put("isNew", isNew);

        return retMap;
    }

}
