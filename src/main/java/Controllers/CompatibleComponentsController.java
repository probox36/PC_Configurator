package Controllers;

import Database.Database;
import Entities.Component;
import Entities.Computer;
import Entities.ComputerJsonModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CompatibleComponentsController {

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/getCompatibleComponentsByComputer")
    public List<Component> getCompatibleComponentsByComputer(@RequestBody String queryJson) {

        List<Component> response = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(queryJson);
            String requiredPartClass = rootNode.get("requiredPartClass").asText();

            JsonNode computerNode = rootNode.get("computer");
            ComputerJsonModel model = objectMapper.treeToValue(computerNode, ComputerJsonModel.class);
            Computer computer = model.convertToComputer();

            Database database = new Database();
            List<Component> components = database.getComponents(requiredPartClass);
            response = components.stream()
                            .filter(component -> computer.isCompatible(component).toBoolean())
                            .toList();

        } catch (JsonProcessingException e) {
            System.out.println("Cannot deserialize following json:\n" + queryJson);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        return response;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/getCompatibleComponentsByPart")
    public List<Component> getCompatibleComponentsByPart(@RequestBody String queryJson) {

        List<Component> response = new ArrayList<>();

        try {
            Database database = new Database();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(queryJson);
            JsonNode partNode = rootNode.get("part");

            String requiredPartClass = rootNode.get("requiredPartClass").asText();
            String partClass = partNode.get("partClass").asText();
            int partId = partNode.get("partId").asInt();

            Component part = database.performQuery(
                    String.format("from %s where id = %d", partClass, partId), Component.class
            ).get(0);

            List<Component> components = database.getComponents(requiredPartClass);
            response = components.stream()
                    .filter(part::isCompatible)
                    .toList();

        } catch (JsonProcessingException e) {
            System.out.println("Cannot deserialize following json:\n" + queryJson);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        return response;
    }

}
