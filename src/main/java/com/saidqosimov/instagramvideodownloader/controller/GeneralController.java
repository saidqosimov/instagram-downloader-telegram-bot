package com.saidqosimov.instagramvideodownloader.controller;

import com.saidqosimov.instagramvideodownloader.enums.MessageType;
import com.saidqosimov.instagramvideodownloader.enums.PostType;
import com.saidqosimov.instagramvideodownloader.model.CodeMessage;
import com.saidqosimov.instagramvideodownloader.service.InstagramService;
import com.saidqosimov.instagramvideodownloader.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class GeneralController {

    private final InstagramService instagramService;
    private final UserService userService;

    public GeneralController(InstagramService instagramService, UserService userService) {
        this.instagramService = instagramService;
        this.userService = userService;
    }

    public List<CodeMessage> handle(Message message) {

        List<CodeMessage> codeMessageList = new LinkedList<>();
        Long chatId = message.getChatId();
        String text = message.getText();
        if (userService.checkUser(chatId)) {
            userService.save(message);
        }
        if (text.equals("/start")) {
            CodeMessage codeMessage = new CodeMessage();
            codeMessage.setMessageType(MessageType.SEND_MESSAGE);
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("Assalomu alaykum.Ushbu bot : \n" +
                            " • Instagramdan postlarni yuklab beradi" +
                            "Buning uchun menga postning linkini uzating\n" +
                            "Masalan : https://www.instagram.com/reel/C22ZDbLuNo5/")
                    .build();
            codeMessage.setSendMessage(sendMessage);
            codeMessageList.add(codeMessage);
            return codeMessageList;
        } else if (text.equals("/help")) {
            CodeMessage codeMessage = new CodeMessage();
            codeMessage.setMessageType(MessageType.SEND_MESSAGE);
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(
                            "Instagramdan postlarni yuklash uchun postning linkini uzating\n" +
                                    "Masalan : https://www.instagram.com/reel/C22ZDbLuNo5/")
                    .build();
            codeMessage.setSendMessage(sendMessage);
            codeMessageList.add(codeMessage);
            return codeMessageList;
        } else if (text.trim().startsWith("https://www.instagram.com/")) {
            List<Map<PostType, String>> result = instagramService.getPostData(text.trim());
            for (Map<PostType, String> map : result) {
                if (map.containsKey(PostType.VIDEO)) {
                    CodeMessage codeMessage = CodeMessage.builder()
                            .messageType(MessageType.SEND_VIDEO)
                            .sendVideo(SendVideo.builder()
                                    .video(new InputFile(map.get(PostType.VIDEO)))
                                    .caption("https://t.me/QosimovBlog")
                                    .chatId(chatId)
                                    .build())
                            .url(map.get(PostType.VIDEO))
                            .build();
                    codeMessageList.add(codeMessage);
                } else if (map.containsKey(PostType.PHOTO)) {
                    CodeMessage codeMessage = CodeMessage.builder()
                            .messageType(MessageType.SEND_PHOTO)
                            .sendPhoto(SendPhoto.builder()
                                    .photo(new InputFile(map.get(PostType.PHOTO)))
                                    .caption("https://t.me/QosimovBlog")
                                    .chatId(chatId)
                                    .build())
                            .url(map.get(PostType.VIDEO))
                            .build();
                    codeMessageList.add(codeMessage);
                }
            }
            return codeMessageList;
        }
        CodeMessage codeMessage = new CodeMessage();
        codeMessage.setMessageType(MessageType.SEND_MESSAGE);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("ERROR!!!")
                .build();
        codeMessage.setSendMessage(sendMessage);
        codeMessageList.add(codeMessage);
        return codeMessageList;
    }

}
