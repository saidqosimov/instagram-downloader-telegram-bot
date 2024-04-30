package com.saidqosimov.instagramvideodownloader.repository;

import com.saidqosimov.instagramvideodownloader.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUsersByChatId(Long chatId);
}
