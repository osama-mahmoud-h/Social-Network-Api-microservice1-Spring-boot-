package com.example.server.repository;

import com.example.server.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {

    List<Message> getMessageBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<Message> getMessageByReceiverIdAndSenderId(Long senderId, Long receiverId);
}
