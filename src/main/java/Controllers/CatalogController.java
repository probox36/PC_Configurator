package Controllers;

import Entities.CompatibilityWrapper;
import Database.Database;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CatalogController {

    @CrossOrigin
    @GetMapping(value = "/getCatalog/{componentType}")
    public List<CompatibilityWrapper> getCatalog(@PathVariable String componentType) {
        System.out.println("Trying to fetch " + componentType);
        Database db = new Database();
        List<CompatibilityWrapper> components;
        try {
            components = db.getComponents(componentType).stream().map(
                    component -> new CompatibilityWrapper(component, true, null)
            ).toList();
            System.out.println(components.get(0).part().getComponentName());
            System.out.println("Got " + components);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(
                    HttpStatusCode.valueOf(404),
                    "Resource not found (" + componentType + ")"
            );
        }
        return components;
    }

}
