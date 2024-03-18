package Controllers;

import Entities.Component;
import Database.Database;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class CatalogController {

    @CrossOrigin
    @GetMapping(value = "/getCatalog/{componentType}")
    public ArrayList<Component> getCatalog(@PathVariable String componentType) {
        System.out.println("Trying to fetch " + componentType);
        Database db = new Database();
        ArrayList<Component> components = new ArrayList<>();
        try {
            components = db.getComponents(componentType);
            System.out.println("Got " + components);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return components;
    }

}
