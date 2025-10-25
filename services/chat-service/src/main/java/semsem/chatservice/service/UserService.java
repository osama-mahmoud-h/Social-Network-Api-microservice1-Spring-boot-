package semsem.chatservice.service;



import semsem.chatservice.model.AppUser;

import java.util.List;

public interface UserService {


    List<AppUser> getConnectedUsers();
}
