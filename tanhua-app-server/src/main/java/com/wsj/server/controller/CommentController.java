package com.wsj.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wsj.server.service.CommentService;
import com.wsj.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity findComments(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize,
                                       String movementId){
        PageResult pr = commentService.findComments(movementId,page,pagesize);
        return ResponseEntity.ok(pr);
    }

    @PostMapping
    public ResponseEntity saveComments(@RequestBody Map<String,Object> map){
        String movementId = (String) map.get("movementId");
        String comment = (String) map.get("comment");
        commentService.saveComments(movementId,comment);
        return ResponseEntity.ok(null);
    }

}
