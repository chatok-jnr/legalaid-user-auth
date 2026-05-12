package com.legalaid.userauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.cloudinary")
@Getter
@Setter
public class CloudinaryProperties {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
    private String uploadFolder;
    private String uploadUrlTemplate = "https://api.cloudinary.com/v1_1/%s/auto/upload";
}

