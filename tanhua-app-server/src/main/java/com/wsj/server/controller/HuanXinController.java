package com.wsj.server.controller;

import com.wsj.server.service.HuanXinService;
import com.wsj.vo.HuanXinUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/huanxin")
public class HuanXinController {

    @Autowired
    private HuanXinService huanXinService;

    @GetMapping("/user")
    public ResponseEntity user(){
        HuanXinUserVo vo = huanXinService.findHuanXinUser();
        return ResponseEntity.ok(vo);
    }
}
