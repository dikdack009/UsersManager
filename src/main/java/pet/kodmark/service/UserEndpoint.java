package pet.kodmark.service;

import io.spring.guides.gs_producing_web_service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

@Endpoint
public class UserEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    private final UserService userService;

    @Autowired
    public UserEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        GetUserResponse response = new GetUserResponse();
        User user = userService.findUser(request.getLogin());
        response.setUser(user);
        System.out.println(userService.getRolesFromString(user));
        userService.getRolesFromString(user).forEach(role -> response.getRoles().add(role));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addUserRequest")
    @ResponsePayload
    public AddUserResponse addUser(@RequestPayload AddUserRequest request) {
        AddUserResponse response = new AddUserResponse();
        List<String> errors = userService.saveUser(request.getUser(), request.getRoles());
        if (errors.isEmpty()) {
            response.setSuccess("true");
        }else {
            response.setSuccess("false");
            errors.forEach(e -> response.getError().add(e));
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteUserRequest")
    @ResponsePayload
    public DeleteUserResponse deleteUser(@RequestPayload DeleteUserRequest request) {
        DeleteUserResponse response = new DeleteUserResponse();
        userService.removeUser(request.getLogin());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateUserRequest")
    @ResponsePayload
    public UpdateUserResponse deleteUser(@RequestPayload UpdateUserRequest request) {
        UpdateUserResponse response = new UpdateUserResponse();
        List<String> errors = userService.updateUser(request.getUser(), request.getRoles());
        if (errors.isEmpty()) {
            response.setSuccess("true");
        }else {
            response.setSuccess("false");
            errors.forEach(e -> response.getError().add(e));
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllUsersRequest")
    @ResponsePayload
    public GetAllUsersResponse getUserList(@RequestPayload GetAllUsersRequest request) {
        GetAllUsersResponse response = new GetAllUsersResponse();

        userService.getAllUser().forEach(user -> response.getUser().add(user));

        return response;
    }
}
