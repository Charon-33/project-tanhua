package com.wsj.dubbo.apiImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.domain.Question;
import com.wsj.dubbo.api.QuestionApi;
import com.wsj.dubbo.mapper.QuestionMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class QuestionApiImpl implements QuestionApi {

    @Resource
    private QuestionMapper questionMapper;

    @Override
    public Question findByUserId(Long userId) {
        QueryWrapper<Question> qw = new QueryWrapper<>();
        qw.eq("user_id",userId);
        return questionMapper.selectOne(qw);
    }

    @Override
    public void save(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public void update(Question question) {
        questionMapper.updateById(question);
    }
}
