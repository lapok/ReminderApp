package org.example.reminderapp.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Service
public class TelegramService extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @PostConstruct
    public void init(){
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("Бот запущен: " + botUsername);
        } catch (TelegramApiException e){
            System.err.println("Ошибка запуска бота: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update){
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            System.out.println("Получено сообщение от " + chatId + ": " + messageText);

            if("/start".equals(messageText)) {
                sendTelegramMessage(chatId, "Привет! Я бот для отправки напоминаний.");
            }
        }
    }

    public void sendTelegramMessage(String chatId, String message) {
        System.out.println("вход в метод sendTelegramMessage");
        System.out.println("chatId = " + chatId);
        System.out.println("message = " + message);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try{
            System.out.println("Отправка запроса в Telegram API...");
            execute(sendMessage);
            System.out.println("Telegram сообщение успешно отправлено в чат " + chatId);
        } catch (TelegramApiException e){
            System.err.println("Ошибка отправки Telegram: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
