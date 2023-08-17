package com.wsj.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.wsj.domain.UserInfo;
import com.wsj.dubbo.api.MovementApi;
import com.wsj.dubbo.api.UserInfoApi;
import com.wsj.mongo.Movement;
import com.wsj.server.exception.BusinessException;
import com.wsj.server.interceptor.UserHolder;
import com.wsj.server.service.MovementService;
import com.wsj.template.OssQiniu;
import com.wsj.utils.Constants;
import com.wsj.vo.ErrorResult;
import com.wsj.vo.MovementsVo;
import com.wsj.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovementServiceImpl implements MovementService {

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private OssQiniu ossQiniu;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        //1、判断发布动态的内容是否存在
        if (StringUtils.isEmpty(movement.getTextContent())) {
            throw new BusinessException(ErrorResult.contentError());
        }

        // 将文件内容上传到阿里云OSS，获取请求地址
        List<String> medias = new ArrayList<>();
        for (MultipartFile multipartFile : imageContent) {
            String upload = ossQiniu.upload2Qiniu(multipartFile.getBytes());
            medias.add(upload);
        }
        movement.setMedias(medias);

        Long userId = UserHolder.getUserId();
        movement.setUserId(userId);
        movementApi.publish(movement);

    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        //1、根据用户id，调用API查询个人动态内容（PageResult  -- Movement）
        PageResult pr = movementApi.findByUserId(userId, page, pagesize);
        //2、获取PageResult中的item列表对象
        List<Movement> items = (List<Movement>) pr.getItems();
        //3、非空判断
        if (items == null) {
            return pr;
        }
        //4、循环数据列表
        UserInfo userInfo = userInfoApi.findById(userId);
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement item : items) {
            //5、一个Movement构建一个Vo对象
            MovementsVo vo = MovementsVo.init(userInfo, item);
            vos.add(vo);
        }
        //6、构建返回值
        pr.setItems(vos);
        return pr;
    }

    //查询好友动态
    @Override
    public PageResult findFriendMovements(Integer page, Integer pagesize) {
        //1、获取当前用户的id
        Long userId = UserHolder.getUserId();
        //2、调用API查询当前用户好友发布的动态列表
        List<Movement> list = movementApi.findFriendMovements(page, pagesize, userId);
        //3、判断列表是否为空
        return getPageResult(page, pagesize, list);
    }

    //查询推荐动态
    @Override
    public PageResult findRecommendMovements(Integer page, Integer pagesize) {
        //1、从redis中获取推荐数据
        String redisKey = Constants.MOVEMENTS_RECOMMEND + UserHolder.getUserId();
        String redisValue = (String) redisTemplate.opsForValue().get(redisKey);
        //2、判断推荐数据是否存在
        List<Movement> list = Collections.EMPTY_LIST;
        if (StringUtils.isEmpty(redisValue)) {
            //3、如果不存在，调用API随机构造10条动态数据
            list = movementApi.randomMoveMents(pagesize);
        } else {
            //4、如果存在，处理pid数据   "16,17,18,19,20,21,10015,10020,10040,10064,10092,10093,10099,10067" 15
            String[] values = redisValue.split(",");
            //判断当前页的起始条数是否小于数组总数
            if ((page - 1) * pagesize < values.length) {
                // 根据 list 分页
                List<Long> pids = Arrays.stream(values).skip((page - 1) * pagesize)
                        .limit(pagesize).map(e -> Long.valueOf(e))
                        .collect(Collectors.toList());
                //5、调用API根据PID数组查询动态数据
                list = movementApi.findMovementsByPids(pids);
            }

        }
        //6、调用公共方法构造返回值
        return getPageResult(page, pagesize, list);
    }

    //根据id查询
    @Override
    public MovementsVo findById(String movementId) {
        //1、调用api根据id查询动态详情
        Movement movement = movementApi.findById(movementId);
        //2、转化vo对象
        if(movement != null){
            UserInfo userInfo = userInfoApi.findById(movement.getUserId());
            return MovementsVo.init(userInfo,movement);
        }
        return null;
    }

    private PageResult getPageResult(Integer page, Integer pagesize, List<Movement> list) {
        if (CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        //4、提取动态发布人的id列表
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //5、根据用户的id列表获取用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //6、一个Movement构造一个vo对象
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : list) {
            // 根据动态的userId找出这个user的详细信息
            UserInfo userInfo = map.get(movement.getUserId());
            if (userInfo != null) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                //修复点赞状态的bug，判断hashKey是否存在
                String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
                String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
                if (redisTemplate.opsForHash().hasKey(key, hashKey)) {
                    vo.setHasLiked(1);
                }
                //修复喜欢状态的bug，判断hashKey是否存在
                String loveHashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
                if (redisTemplate.opsForHash().hasKey(key, loveHashKey)) {
                    vo.setHasLoved(1);
                }
                vos.add(vo);
            }
        }
        //7、构造PageResult并返回
        return new PageResult(page, pagesize, 0L, vos);
    }
}
