package web.dao;

import org.springframework.security.core.userdetails.UserDetails;
import web.model.User;
import java.util.List;

public interface UserDao {
    void saveUser(User user);
    void removeUser(User user);
    List<User> getAllUser();
    User findById(Long id);
    User findUserByName(String username);
}
