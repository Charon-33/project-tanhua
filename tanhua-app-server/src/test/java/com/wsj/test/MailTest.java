package com.wsj.test;

import com.wsj.server.AppServerApplication;
import com.wsj.template.MailTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class MailTest {

    @Resource
    private MailTemplate mailTemplate;

    @Test
    public void testSendMail(){
        mailTemplate.sendMail("1935496654@qq.com","测试的邮件","练习探花项目");
    }

}
