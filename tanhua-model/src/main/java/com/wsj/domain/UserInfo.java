package com.wsj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    /**
     * 由于userinfo表和user表之间是一对一关系
     * userInfo的id来源于user表的id
     * ----------------------------------
     * 使用 @TableId(type = IdType.INPUT) 注解后，MyBatis-Plus
     * 将不会自动生成主键值，而是使用插入语句中传入的值或手动设置的值作为主键值进行插入操作。
     *-------------------------------------
     */
    @TableId(type = IdType.INPUT)
    private Long id; //用户id
    private String nickname; //昵称
    private String avatar; //用户头像
    private String birthday; //生日
    private String gender; //性别
    private Integer age; //年龄
    private String city; //城市
    private String income; //收入
    private String education; //学历
    private String profession; //行业
    private Integer marriage; //婚姻状态
    private String tags; //用户标签：多个用逗号分隔
    private String coverPic; // 封面图片
    private Date created;
    private Date updated;
}