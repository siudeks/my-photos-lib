package net.siudek.media.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix="telegram")
@Data
public class TelegramProperties {
    private String secret;
}
