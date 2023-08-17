package com.wsj.test;

import com.wsj.domain.User;
import com.wsj.dubbo.api.UserApi;
import com.wsj.server.AppServerApplication;
import com.wsj.template.HuanXinTemplate;
import com.wsj.utils.Constants;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class HuanXinTest {

    @Resource
    private HuanXinTemplate template;
    @DubboReference
    private UserApi userApi;

    @Test
    public void testRigister(){
        template.createUser("user01","123456");
    }

    @Test
    public void register(){
//        Boolean create = template.createUser("hx-121","123456");
        List<User> userList = userApi.findAll();
        int i = 0;
        for (User user : userList) {
            Boolean create = template.createUser("hx-"+user.getId(),"123456");
            if(create){
                user.setHxUser("hx"+user.getId());
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.update(user);
            }

            i++;
            if(i == 10){
                break;
            }
        }
    }
}
