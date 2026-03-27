package org.example.reminderapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class TelegramService extends TelegramLongPollingBot {


    private final String botToken;

    private final String botUsername;

    public TelegramService(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update){
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            log.info("Получено сообщение от {}: {}", chatId, messageText);

            if("/start".equals(messageText)) {
                sendTelegramMessage(chatId, "Привет! Я бот для отправки напоминаний.");
            }
        }
    }

    public void sendTelegramMessage(String chatId, String message) {
        log.debug("Подготовка к отправке сообщения в чат: {}", chatId);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();

        try{
            execute(sendMessage);
            log.info("Сообщение успешно отправлено в чат {}", chatId);
        } catch (TelegramApiException e){
            log.error("Ошибка при отправке сообьщения: {}", e.getMessage());
        }
    }
}
