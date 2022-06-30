package pet.kodmark.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import io.spring.guides.gs_producing_web_service.User;
import io.spring.guides.gs_producing_web_service.Role;
import pet.kodmark.model.Key;
import pet.kodmark.model.UserRoles;
import pet.kodmark.repository.MySQLConnection;

import java.util.*;

@Component
public class UserRepository {
    @SuppressWarnings("unchecked")
    public List<User> getAllUser() {
        Session session = MySQLConnection.openConnection();
        List<pet.kodmark.model.User> result = session.createQuery("FROM " + pet.kodmark.model.User.class.getSimpleName()).getResultList();
        session.close();
        List<User> userList = new LinkedList<>();

        result.forEach(user -> {
            User newUser = new User();
            newUser.setName(user.getName());
            newUser.setLogin(user.getLogin());
            newUser.setPassword(user.getPassword());
            userList.add(newUser);
        });
        return userList;
    }

    public User findUser(String login) {
        if (login == null || login.isEmpty()){
            throw new RuntimeException("The user's login must not be null");
        }
        Session session = MySQLConnection.openConnection();
        Transaction transaction = session.beginTransaction();
        pet.kodmark.model.User result = session.get(pet.kodmark.model.User.class, login);
        if (result == null ){
            throw new RuntimeException("The user '" + login + "' not found");
        }
        session.close();

        User user = new User();
        user.setName(result.getName());
        user.setLogin(result.getLogin());
        user.setPassword(result.getPassword());
        return user;
    }
    @SuppressWarnings("unchecked")
    public List<Role> getRolesFromString(User user){
        Session session = MySQLConnection.openConnection();
        List<pet.kodmark.model.Role> roleList =
                session.createQuery("SELECT s.role FROM " + pet.kodmark.model.UserRoles.class.getSimpleName() + " AS s " +
                        "WHERE s.user = '" + user.getLogin() + "'" ).getResultList();
        session.close();
        List<Role> result = new ArrayList<>();
        roleList.forEach(role -> {
            Role newRole = new Role();
            newRole.setId(role.getId());
            newRole.setName(role.getName());
            result.add(newRole);
        });
        roleList.forEach(System.out::println);
        result.forEach(System.out::println);
        return result;
    }

    @SuppressWarnings("unchecked")
    public void removeUser(String login){
        if (login == null || login.isEmpty()){
            throw new RuntimeException("The user's login must not be null");
        }
        Session session = MySQLConnection.openConnection();
        Transaction transaction = session.beginTransaction();
        pet.kodmark.model.User result = session.get(pet.kodmark.model.User.class, login);
        if (result == null ){
            throw new RuntimeException("The user '" + login + "' not found");
        }

        result.getRoles().forEach(session::delete);
        result.getRoles().clear();
        List<pet.kodmark.model.Role> roleList =
                session.createQuery("SELECT s.role FROM " + pet.kodmark.model.UserRoles.class.getSimpleName() + " AS s " +
                        "WHERE s.user = '" + login + "'" ).getResultList();
        roleList.forEach(session::delete);
        System.out.println(result + " " + result.getRoles());
        session.delete(result);
        transaction.commit();
        session.close();
    }

    public List<String> saveUser(User user, String roleName) {
        List<String> message = checkData(user);
        List<pet.kodmark.model.Role> roleList = new ArrayList<>();
        Session session = MySQLConnection.openConnection();
        if (roleName == null || roleName.isEmpty()) {
            message.add("The user's roles must not be null");
        } else {
            roleList = getRolesFromString(roleName);
            for (String role : roleName.toLowerCase(Locale.ROOT).split(",\\s?")) {
                if (session.createQuery("from " + pet.kodmark.model.Role.class.getSimpleName() + " as s where s.name = '" + role + "'").getResultList().isEmpty()){
                    message.add("The role '" + role + "' not found");
                }
            }
        }

        pet.kodmark.model.User result = session.get(pet.kodmark.model.User.class, user.getLogin());
        session.close();
        if (result != null) {
            message.add("The user '" + user.getLogin() + "' already exist");
        }
        if (!message.isEmpty()){
            return message;
        }

        addUserRoles(user, roleList);
        return message;
    }

    @SuppressWarnings("unchecked")
    public List<String> updateUser(User user, String roleName) {
        List<String> message = checkData(user);
        Session session = MySQLConnection.openConnection();
        pet.kodmark.model.User result = session.get(pet.kodmark.model.User.class, user.getLogin());
        if (result == null) {
            message.add("The user '" + user.getLogin() + "' not found");
        }
        if (roleName != null && !roleName.equals("")) {
            for (String role : roleName.toLowerCase(Locale.ROOT).split(",\\s?")) {
                if (session.createQuery("from " + pet.kodmark.model.Role.class.getSimpleName() + " as s where s.name = '" + role + "'").getResultList().isEmpty()) {
                    message.add("The role '" + role + "' not found");
                }
            }
        }
        if (!message.isEmpty()){
            return message;
        }

        assert result != null;
        result.getRoles().forEach(session::delete);
        result.getRoles().clear();
        pet.kodmark.model.User newUser =
                new pet.kodmark.model.User(user.getName(), user.getLogin(), user.getPassword());
        session = MySQLConnection.openConnection();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(newUser);
        transaction.commit();
        session.close();
        session = MySQLConnection.openConnection();
        List<pet.kodmark.model.Role> roleList =
                session.createQuery("SELECT s.role FROM " + pet.kodmark.model.UserRoles.class.getSimpleName() + " AS s " +
                        "WHERE s.user = '" + result.getLogin() + "'" ).getResultList();
        session.close();

        User otherUser = new User();
        otherUser.setName(user.getName());
        otherUser.setLogin(user.getLogin());
        otherUser.setPassword(user.getPassword());
        if (roleName.isEmpty()) {
            addUserRoles(otherUser, roleList);

        } else {
            session = MySQLConnection.openConnection();
            transaction = session.beginTransaction();
            List<pet.kodmark.model.UserRoles> userRoles =
                    session.createQuery("FROM " + pet.kodmark.model.UserRoles.class.getSimpleName() + " AS s " +
                            "WHERE s.user = '" + result.getLogin() + "'" ).getResultList();
            userRoles.forEach(session::delete);
            transaction.commit();
            session.close();
            addUserRoles(otherUser, getRolesFromString(roleName));
        }


        return message;
    }

    @SuppressWarnings("unchecked")
    private List<pet.kodmark.model.Role> getRolesFromString(String roleName){
        Session session = MySQLConnection.openConnection();
        StringJoiner stringJoiner =
                new StringJoiner("' OR name = '", "FROM " + pet.kodmark.model.Role.class.getSimpleName() + " WHERE name = '", "'");
        Arrays.stream(roleName.toLowerCase(Locale.ROOT).split(",\\s?")).toList().forEach(stringJoiner::add);
        List<pet.kodmark.model.Role> roleList =  session.createQuery(stringJoiner.toString()).getResultList();
        session.close();
        return roleList;
    }

    private List<String> checkData(User user){
        List<String> message = new ArrayList<>();
        if (user.getName() == null || user.getName().isEmpty()) {
            message.add("The user's name must not be null");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            message.add("The user's login must not be null");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            message.add("The user's password must not be null");
        }
        else if (!isValid(user.getPassword())) {
            message.add("The user's password must contain a letter in uppercase and a number");
        }
        return message;
    }


    private boolean isValid(String s) {
        return s.matches(".*\\d+.*") && s.matches(".*[A-Z]+.*");
    }

    private void addUserRoles(User user, List<pet.kodmark.model.Role> roleList){
        Session session;
        Transaction transaction;

        pet.kodmark.model.User newUser = new pet.kodmark.model.User(user.getName(), user.getLogin(), user.getPassword());
        for (pet.kodmark.model.Role role : roleList) {
            session = MySQLConnection.openConnection();
            transaction = session.beginTransaction();

            UserRoles userRoles = new UserRoles();
            userRoles.setId(new Key(newUser.getLogin(), role.getId()));
            userRoles.setUser(newUser);
            userRoles.setRole(role);

            session.saveOrUpdate(userRoles);
            transaction.commit();
            session.close();
        }
        session = MySQLConnection.openConnection();
        session.save(newUser);
        session.close();
    }
}
