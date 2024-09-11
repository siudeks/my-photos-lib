package net.siudek.media.telegram;


import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Configuration
@ConfigurationPropertiesScan
public class MyConfiguration {
    
    @Bean
    TelegramBotsLongPollingApplication newTelegramBotsLongPollingApplication() {
        return new TelegramBotsLongPollingApplication();
    }
}
