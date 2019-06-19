package com.fyqz.config;

import com.fyqz.util.InstanceUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "fyqz.ignorepath")
@Data
public class IgnoreProperties {
     List<String> list= InstanceUtil.newArrayList();
}