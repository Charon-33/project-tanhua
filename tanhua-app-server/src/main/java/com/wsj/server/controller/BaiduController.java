package com.wsj.server.controller;

import com.wsj.server.service.BaiduService;
import com.wsj.server.service.TanhuaService;
import com.wsj.vo.NearUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/baidu")
public class BaiduController {

    @Autowired
    private BaiduService baiduService;
    @Autowired
    private TanhuaService tanhuaService;

    @GetMapping("/location")
    public ResponseEntity updateLocation(@RequestBody Map param){
        Double longitude = Double.valueOf(param.get("longitude").toString());
        Double latitude = Double.valueOf(param.get("latitude").toString());
        String address = param.get("addrStr").toString();
        baiduService.updateLocation(longitude,latitude,address);
        return ResponseEntity.ok(null);
    }

    /**
     * 搜附近
     */
    @GetMapping("/search")
    public ResponseEntity<List<NearUserVo>> queryNearUser(String gender, @RequestParam(defaultValue = "2000") String distance){
        List<NearUserVo> list = tanhuaService.queryNearUser(gender,distance);
        return ResponseEntity.ok(list);
    }
}
