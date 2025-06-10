package semsem.chatservice.service;



import semsem.chatservice.model.AppUser;

import java.util.List;

public interface UserService {
    void saveUser(AppUser user);

    void disconnect(AppUser user);

    List<AppUser> getConnectedUsers();
}
