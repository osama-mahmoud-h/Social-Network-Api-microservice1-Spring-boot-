package semsem.chatservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import semsem.chatservice.enums.UserStatus;
import semsem.chatservice.model.AppUser;
import semsem.chatservice.repository.AppUserRepository;
import semsem.chatservice.repository.RedisOnlineUserRepository;
import semsem.chatservice.service.UserService;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AppUserRepository appUserRepository;
    private final RedisOnlineUserRepository redisOnlineUserRepository;

    @Override
    public void saveUser(AppUser user) {
        AppUser storedUser = appUserRepository.findOneByNickName(user.getNickName())
                .orElseGet(()-> appUserRepository.save(user));

        if(storedUser.getStatus() == UserStatus.OFFLINE) {
            storedUser.setStatus(UserStatus.ONLINE);
            appUserRepository.save(storedUser);
        }

        // Store user as online in Redis
        redisOnlineUserRepository.addOnlineUser(storedUser.getId());
    }

    @Override
    public void disconnect(AppUser user) {
        System.out.println("disconnecting user: service-layer " + user);
       AppUser storedUser = appUserRepository.findOneByNickName(user .getNickName()).map(
                u -> {
                     u.setStatus(UserStatus.OFFLINE);
                     // Remove user from online users in Redis
                     redisOnlineUserRepository.removeOnlineUser(u.getId());
                     return appUserRepository.save(u);
                }
         ).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<AppUser> getConnectedUsers() {
        return appUserRepository.findAllByStatus(UserStatus.ONLINE);
    }
}
