package org.example.reminderapp;

import org.example.reminderapp.config.TestBotConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestBotConfig.class)
class ReminderAppApplicationTests {

    @Test
    @Disabled
    void contextLoads() {
    }

}
