package com.wsj.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.wsj.domain.Question;
import com.wsj.domain.UserInfo;
import com.wsj.dto.RecommendUserDto;
import com.wsj.dubbo.api.*;
import com.wsj.mongo.RecommendUser;
import com.wsj.server.exception.BusinessException;
import com.wsj.server.interceptor.UserHolder;
import com.wsj.server.service.MessagesService;
import com.wsj.server.service.TanhuaService;
import com.wsj.template.HuanXinTemplate;
import com.wsj.utils.Constants;
import com.wsj.vo.ErrorResult;
import com.wsj.vo.NearUserVo;
import com.wsj.vo.PageResult;
import com.wsj.vo.TodayBest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TanhuaServiceImpl implements TanhuaService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private UserLikeApi userLikeApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MessagesService messagesService;

    @DubboReference
    private UserLocationApi userLocationApi;

    //指定默认数据
    @Value("${tanhua.default.recommend.users}")
    private String recommendUser;

    @Override
    public TodayBest todayBest() {
        //1、获取用户id
        Long userId = UserHolder.getUserId();
        //2、调用API查询
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(99d);
        }
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        //3、将RecommendUser转化为TodayBest对象
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        return vo;
    }

    @Override
    public PageResult recommendation(RecommendUserDto dto) {
        //1、获取用户id
        Long userId = UserHolder.getUserId();
        //2、调用recommendUserApi分页查询数据列表（PageResult -- RecommendUser）
        PageResult pr = recommendUserApi.queryRecommendUserList(dto.getPage(), dto.getPagesize(), userId);
        //3、获取分页中的RecommendUser数据列表
        List<RecommendUser> items = (List<RecommendUser>) pr.getItems();
        //4、判断列表是否为空
        if (items == null) {
            return pr;
        }
        //5、提取所有推荐的用户id列表
        List<Long> ids = CollUtil.getFieldValues(items, "userId", Long.class);
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(dto.getAge());
        userInfo.setGender(dto.getGender());
        //6、构建查询条件，批量查询所有的用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);
        //7、循环推荐的数据列表，构建vo对象
        List<TodayBest> list = new ArrayList<>();
        for (RecommendUser item : items) {
            // mongodb中的userid  map中的userid
            UserInfo info = map.get(item.getUserId());
            if (info != null) {
                TodayBest vo = TodayBest.init(info, item);
                list.add(vo);
            }
        }
        //8、构造返回值
        pr.setItems(list);
        return pr;
    }

    //查看佳人详情
    @Override
    public TodayBest personalInfo(Long userId) {
        //1、根据用户id查询，用户详情
        UserInfo userInfo = userInfoApi.findById(userId);
        //2、根据操作人id和查看的用户id，查询两者的推荐数据
        RecommendUser user = recommendUserApi.queryByUserId(userId, UserHolder.getUserId());
        //3、构造返回值
        return TodayBest.init(userInfo, user);
    }

    /**
     * 查询用户陌生人问题
     */
    @Override
    public String strangerQuestions(Long userId) {
        Question question = questionApi.findByUserId(userId);
        return question == null ? "你喜欢java编程吗？" : question.getTxt();
    }

    //回复陌生人问题
    @Override
    public void replyQuestions(Long userId, String reply) {
        //1、构造消息数据
        Long currentUserId = UserHolder.getUserId();
        UserInfo userInfo = userInfoApi.findById(userId);
        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(currentUserId));
        map.put("huanxinId", Constants.HX_USER_PREFIX + currentUserId);
        map.put("nickname", userInfo.getNickname());
        map.put("strangerQuestion", strangerQuestions(userId));
        map.put("reply", reply);
        String message = JSON.toJSONString(map);
        //2、调用template对象，发送消息，参数：接受方的环信id，消息
        Boolean aBoolean = huanXinTemplate.sendMsg(Constants.HX_USER_PREFIX + userId, message);
        if (!aBoolean) {
            throw new BusinessException(ErrorResult.error());
        }
    }

    @Override
    public List<TodayBest> queryCardsList() {
        //1、调用推荐API查询数据列表（排除喜欢/不喜欢的用户，数量限制）
        List<RecommendUser> users = recommendUserApi.queryCardsList(UserHolder.getUserId(), 10);
        //2、判断数据是否存在，如果不存在，构造默认数据 1,2,3
        if (CollUtil.isEmpty(users)) {
            users = new ArrayList<>();
            String[] userIds = recommendUser.split(",");
            for (String userId : userIds) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(userId));
                recommendUser.setToUserId(UserHolder.getUserId());
                recommendUser.setScore(RandomUtil.randomDouble(80, 90));
                users.add(recommendUser);
            }
        }
        //3、构造VO
        List<Long> ids = CollUtil.getFieldValues(users, "userId", Long.class);
        Map<Long, UserInfo> infoMap = userInfoApi.findByIds(ids, null);

        List<TodayBest> vos = new ArrayList<>();
        for (RecommendUser user : users) {
            UserInfo userInfo = infoMap.get(user.getUserId());
            if (userInfo != null) {
                TodayBest vo = TodayBest.init(userInfo, user);
                vos.add(vo);
            }
        }

        return vos;
    }

    /**
     * 右滑喜欢
     */
    @Override
    public void likeUser(Long likeUserId) {
        //1、调用API，保存喜欢数据(保存到MongoDB中)
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(), likeUserId, true);
        if (!save) {
            //失败
            throw new BusinessException(ErrorResult.error());
        }
        //2、操作redis，写入喜欢的数据，删除不喜欢的数据 (喜欢的集合，不喜欢的集合)
        redisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        redisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        //3、判断是否双向喜欢
        if (isLike(likeUserId, UserHolder.getUserId())) {
            //4、添加好友
            messagesService.contacts(likeUserId);
        }
    }

    /**
     * 左滑不喜欢
     */
    public void notLikeUser(Long likeUserId) {
        //1、调用API，保存喜欢数据(保存到MongoDB中)
        Boolean save = userLikeApi.saveOrUpdate(UserHolder.getUserId(), likeUserId, false);
        if (!save) {
            //失败
            throw new BusinessException(ErrorResult.error());
        }
        //2、操作redis，写入喜欢的数据，删除不喜欢的数据 (喜欢的集合，不喜欢的集合)
        redisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        redisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        //3. 判断是否双向喜欢，删除好友(后续实现)
    }

    //搜附近
    @Override
    public List<NearUserVo> queryNearUser(String gender, String distance) {
        //1、调用API查询附近的用户（返回的是附近的人的所有用户id，包含当前用户的id）
        List<Long> userIds = userLocationApi.queryNearUser(UserHolder.getUserId(), Double.valueOf(distance));
        //2、判断集合是否为空
        if (CollUtil.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        //3、调用UserInfoApi根据用户id查询用户详情
        UserInfo userInfo = new UserInfo();
        userInfo.setGender(gender);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
        //4、构造返回值。
        List<NearUserVo> vos = new ArrayList<>();
        for (Long userId : userIds) {
            //排除当前用户
            if (userId == UserHolder.getUserId()) {
                continue;
            }
            UserInfo info = map.get(userId);
            if (info != null) {
                NearUserVo vo = NearUserVo.init(info);
                vos.add(vo);
            }
        }
        return vos;
    }

    //判断是否双向喜欢
    public Boolean isLike(Long userId, Long likeUserId) {
        String key = Constants.USER_LIKE_KEY + userId;
        return redisTemplate.opsForSet().isMember(key, likeUserId.toString());
    }
}
