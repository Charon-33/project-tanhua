package com.wsj.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tanhua.mail")
public class MailProperties {
    private String user;
    private String password;
}
