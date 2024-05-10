package Controllers;

import Database.Database;
import Database.HibernateConfigurator;
import Entities.User;
import Enums.AuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private User parseUser(String userJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(userJson);
        return objectMapper.treeToValue(rootNode, User.class);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/login")
    public AuthResponse login(@RequestBody String userJson) {

        AuthResponse response = AuthResponse.Error;

        try {

            User newUser = parseUser(userJson);
            Database db = new Database();
            List<User> users = db.getByFieldValue(User.class, "userName", newUser.getUserName());

            if (!users.isEmpty()) {
                response = users.get(0).getPassword().equals(newUser.getPassword())
                        ? AuthResponse.Success
                        : AuthResponse.IncorrectPassword;
            } else {
                response = AuthResponse.NoSuchUser;
            }

        } catch (JsonProcessingException e) {
            System.out.println("Cannot deserialize following json:\n" + userJson);
        }

        return response;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/register")
    public AuthResponse register(@RequestBody String userJson) {

        AuthResponse response = AuthResponse.Error;

        try {

            User newUser = parseUser(userJson);
            Database db = new Database();
            List<User> usersWithSameEMail = db.getByFieldValue(User.class, "eMail", newUser.getEMail());
            List<User> usersWithSameUserName = db.getByFieldValue(User.class, "userName", newUser.getUserName());

            if (!usersWithSameEMail.isEmpty()) {
                response = AuthResponse.EmailTaken;
            } else if (!usersWithSameUserName.isEmpty()) {
                response = AuthResponse.LoginTaken;
            } else {
                db.persist(newUser);
                response = AuthResponse.Success;
            }

        } catch (JsonProcessingException e) {
            System.out.println("Cannot deserialize following json:\n" + userJson);
        }

        return response;

    }

}
