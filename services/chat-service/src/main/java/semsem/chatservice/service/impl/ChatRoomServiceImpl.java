package semsem.chatservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semsem.chatservice.model.ChatRoom;
import semsem.chatservice.repository.ChatRoomRepository;
import semsem.chatservice.service.ChatRoomService;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public Optional<String> getChatRoomId(String senderId, String receiverId) {
        String chatId = this.generateChatId(senderId, receiverId);

        return chatRoomRepository.findChatRoomByChatId(chatId)
                .map(ChatRoom::getChatId)
                .or(() -> Optional.of(this.createNewChatRoom(senderId, receiverId)));
    }

    private String createNewChatRoom(String senderId, String receiverId) {
        String chatId = this.generateChatId(senderId, receiverId);

        ChatRoom chatRoom = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .receiverId(receiverId)
                .build();
        chatRoomRepository.save(chatRoom);
        return chatId;
    }

    private String generateChatId(String senderId, String receiverId) {
        return Stream.of(senderId, receiverId)
                .sorted()
                .collect(Collectors.joining("_"));
    }


}
