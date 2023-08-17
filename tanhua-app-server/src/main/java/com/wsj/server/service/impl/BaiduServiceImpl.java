package com.wsj.server.service.impl;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.wsj.dubbo.api.UserLocationApi;
import com.wsj.mongo.UserLocation;
import com.wsj.server.exception.BusinessException;
import com.wsj.server.interceptor.UserHolder;
import com.wsj.server.service.BaiduService;
import com.wsj.vo.ErrorResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaiduServiceImpl implements BaiduService {

    @DubboReference
    private UserLocationApi userLocationApi;

    //更新地理位置
    @Override
    public void updateLocation(Double longitude, Double latitude, String address) {
        Boolean flag = userLocationApi.updateLocation(UserHolder.getUserId(),longitude,latitude,address);
        if(!flag){
            throw new BusinessException(ErrorResult.error());
        }
    }
}
