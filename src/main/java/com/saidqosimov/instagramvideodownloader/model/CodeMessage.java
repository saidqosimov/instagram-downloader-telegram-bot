package com.saidqosimov.instagramvideodownloader.model;

import com.saidqosimov.instagramvideodownloader.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CodeMessage {
    private MessageType messageType;
    private SendMessage sendMessage;
    private EditMessageText editMessageText;
    private DeleteMessage deleteMessage;
    private SendPhoto sendPhoto;
    private SendVideo sendVideo;
    private String url;
}
