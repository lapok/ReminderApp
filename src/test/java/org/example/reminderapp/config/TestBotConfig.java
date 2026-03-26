package org.example.reminderapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestBotConfig {

    @Bean
    @Primary
    public TelegramBotsApi telegramBotsApi() {
        return mock(TelegramBotsApi.class);
    }
}
