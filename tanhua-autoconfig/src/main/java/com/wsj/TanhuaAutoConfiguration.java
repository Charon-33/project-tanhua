package com.wsj;

import com.wsj.properties.MailProperties;
import com.wsj.template.MailTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        MailProperties.class
})
public class TanhuaAutoConfiguration {

    @Bean
    public MailTemplate MailTemplate(MailProperties properties) {
        return new MailTemplate(properties);
    }
}
