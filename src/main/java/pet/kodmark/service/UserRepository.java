package pet.kodmark.service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import io.spring.guides.gs_producing_web_service.User;

@Component
public class UserRepository {
    private static final Map<String, User> users = new HashMap<>();

    @PostConstruct
    public void initData() {
        User user1 = new User("Max", "lox", "loh1998");
        User user2 = new User("Nick", "god", "12345");
        User user3 = new User("Vadik", "molodez", "pazanisosite");

        users.put(user1.getName(), user1);
        users.put(user2.getName(), user2);
        users.put(user3.getName(), user3);
    }

    public User findUser(String login) {
        Assert.notNull(login, "The country's name must not be null");
        return users.get(login);
    }
}
