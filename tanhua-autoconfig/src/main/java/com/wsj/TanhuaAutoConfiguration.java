package com.wsj;

import com.wsj.properties.HuanXinProperties;
import com.wsj.properties.MailProperties;
import com.wsj.properties.OssProperties;
import com.wsj.template.HuanXinTemplate;
import com.wsj.template.MailTemplate;
import com.wsj.template.OssQiniu;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        MailProperties.class,
        OssProperties.class,
        HuanXinProperties.class
})
public class TanhuaAutoConfiguration {

    @Bean
    public MailTemplate MailTemplate(MailProperties properties) {
        return new MailTemplate(properties);
    }

    @Bean
    public OssQiniu ossTemplate(OssProperties properties) {
        return new OssQiniu(properties);
    }

    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties properties){
        return new HuanXinTemplate(properties);
    }
}
