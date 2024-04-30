package com.saidqosimov.instagramvideodownloader.service;

import com.saidqosimov.instagramvideodownloader.entity.User;
import com.saidqosimov.instagramvideodownloader.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public synchronized Boolean checkUser(Long chatId) {
        return userRepository.findUsersByChatId(chatId).isEmpty();
    }

    public synchronized void save(Message message) {
        User user = User.builder()
                .chatId(message.getChatId())
                .firstName(message.getFrom().getFirstName())
                .username(message.getFrom().getUserName())
                .build();
        userRepository.save(user);
    }

}
