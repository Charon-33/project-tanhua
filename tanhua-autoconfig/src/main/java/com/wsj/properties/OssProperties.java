package com.wsj.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tanhua.oss")
public class OssProperties {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String filePlace;
    private String url;
}