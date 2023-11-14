package Components;

import lombok.Data;

@Data
public abstract class Component {
    String modelName;
    String componentName;
    Double price;
    String description;
    double powerConsumption;

    abstract boolean isCompatible(Component component);

    public String toString() {
        return componentName + " " + modelName;
    }
}
