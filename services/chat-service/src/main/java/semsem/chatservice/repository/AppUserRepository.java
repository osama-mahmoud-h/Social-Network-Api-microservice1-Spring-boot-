package semsem.chatservice.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import semsem.chatservice.enums.UserStatus;
import semsem.chatservice.model.AppUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository  extends MongoRepository<AppUser, String> {

    List<AppUser> findAllByStatus(UserStatus userStatus);

    Optional<AppUser> findOneByNickName(String nickName);
}
