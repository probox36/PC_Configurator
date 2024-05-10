package Controllers;

import Database.Database;
import Entities.Order;
import Enums.AuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/createOrder")
    public AuthResponse createOrder(@RequestBody String orderJson) {

        AuthResponse response = AuthResponse.Error;
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Database db = new Database();

        try {
            JsonNode rootNode = objectMapper.readTree(orderJson);
            System.out.println(rootNode.toString());
            Order order = objectMapper.treeToValue(rootNode, Order.class);
            db.persist(order);
            response = AuthResponse.Success;
            System.out.println(order);
        } catch (JsonProcessingException e) {
            System.out.println("Cannot deserialize following json:\n" + orderJson);
            e.printStackTrace();
        }

        return response;

    }

}
