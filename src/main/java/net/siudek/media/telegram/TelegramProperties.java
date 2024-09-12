package net.siudek.media.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="telegram")
public record TelegramProperties(String secret) {
}
