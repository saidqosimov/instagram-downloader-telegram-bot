package com.saidqosimov.instagramvideodownloader.controller;

import com.saidqosimov.instagramvideodownloader.config.BotConfig;
import com.saidqosimov.instagramvideodownloader.model.CodeMessage;
import com.saidqosimov.instagramvideodownloader.service.InstagramService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Component
public class BotController extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final GeneralController generalController;

    public BotController(BotConfig botConfig, GeneralController generalController) {
        this.botConfig = botConfig;
        this.generalController = generalController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String text = message.getText();
            Long chatId = message.getChatId();
            if (text.startsWith("https://www.instagram.com/")) {
                Integer processMessageId = inProcess(chatId);
                sendMsg(generalController.handle(message));
                deleteProcess(chatId, processMessageId);
            } else {
                sendMsg(generalController.handle(message));
            }
        }
    }

    @SneakyThrows
    private synchronized Integer inProcess(Long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("🔎")
                .build();
        return execute(sendMessage).getMessageId();
    }

    @SneakyThrows
    private synchronized void deleteProcess(Long chatId, Integer processMessageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(processMessageId)
                .build();
        execute(deleteMessage);
    }

    private synchronized void sendMsg(List<CodeMessage> messageList) {
        for (CodeMessage message : messageList) {
            switch (message.getMessageType()) {
                case SEND_MESSAGE -> {
                    try {
                        execute(message.getSendMessage());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case EDIT_MESSAGE -> {
                    try {
                        execute(message.getEditMessageText());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case DELETE_MESSAGE -> {
                    try {
                        execute(message.getDeleteMessage());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case SEND_PHOTO -> {
                    try {
                        execute(message.getSendPhoto());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case SEND_VIDEO -> {
                    try {
                        execute(message.getSendVideo());
                    } catch (Exception e) {
                        URL url = null;
                        try {
                            url = new URL(message.getUrl());
                        } catch (MalformedURLException ex) {
                            throw new RuntimeException(ex);
                        }
                        InputStream inputStream = null;
                        try {
                            inputStream = url.openStream();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        InputFile inputFile = new InputFile(inputStream, "video.mp4");
                        SendVideo sendVideo = new SendVideo();
                        sendVideo.setChatId(message.getSendVideo().getChatId());
                        sendVideo.setCaption("@QosimovBlog");
                        sendVideo.setVideo(inputFile);
                        try {
                            execute(sendVideo);
                        } catch (Exception ex) {
                            SendMessage sendMessage = SendMessage
                                    .builder()
                                    .text(message.getUrl())
                                    .chatId(message.getSendVideo().getChatId())
                                    .build();
                            try {
                                execute(sendMessage);
                            } catch (TelegramApiException exc) {
                                throw new RuntimeException(exc);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
