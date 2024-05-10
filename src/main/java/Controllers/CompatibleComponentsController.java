package Controllers;

import Database.Database;
import Entities.CompatibilityWrapper;
import Entities.Component;
import Entities.Computer;
import Entities.ComputerJsonModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CompatibleComponentsController {

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/getCompatibleComponentsByComputer")
    public List<CompatibilityWrapper> getCompatibleComponentsByComputer(@RequestBody String queryJson) {

        List<CompatibilityWrapper> response = new ArrayList<>();
        String requiredPartClass = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(queryJson);
            requiredPartClass = rootNode.get("requiredPartClass").asText();

            JsonNode computerNode = rootNode.get("computer");
            ComputerJsonModel model = objectMapper.treeToValue(computerNode, ComputerJsonModel.class);
            Computer computer = model.convertToComputer();

            Database database = new Database();
            List<Component> components = database.getComponents(requiredPartClass);

            for (Component component: components) {
                Computer.CheckResult result = computer.isCompatible(component);
                response.add(new CompatibilityWrapper(component, result.toBoolean(), result.incompatibility().toString()));
            }

        } catch (JsonProcessingException e) {
            System.out.println("Cannot deserialize following json:\n" + queryJson);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatusCode.valueOf(404),
                    "Resource not found (" + requiredPartClass + ")"
            );
        }

        return response;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/getCompatibleComponentsByPart")
    public List<CompatibilityWrapper> getCompatibleComponentsByPart(@RequestBody String queryJson) {
        List<CompatibilityWrapper> response = new ArrayList<>();
        String requiredPartClass = "";

        try {
            Database database = new Database();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(queryJson);
            JsonNode partNode = rootNode.get("part");

            requiredPartClass = rootNode.get("requiredPartClass").asText();
            String partClass = partNode.get("partClass").asText();
            int partId = partNode.get("partId").asInt();

            Component part = database.performQuery(
                    String.format("from %s where id = %d", partClass, partId), Component.class
            ).get(0);

            List<Component> components = database.getComponents(requiredPartClass);
            response = components.stream().map(
                    component -> new CompatibilityWrapper(component, part.isCompatible(component), part.toString())
            ).toList();

        } catch (JsonProcessingException e) {
            System.out.println("Cannot deserialize following json:\n" + queryJson);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(
                    HttpStatusCode.valueOf(404),
                    "Resource not found (" + requiredPartClass + ")"
            );
        }

        return response;
    }

}