package Components;

import Enums.RAMGeneration;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RAM extends Component {
    private RAMGeneration ramGeneration;
    private double frequency;
    private double capacity;

    public RAM(String modelName) {
        componentName = "Оперативная память";
        stackable = true;
        this.modelName = modelName;
    }


    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Motherboard) {
            return component.isCompatible(this);
        }
        return true;
    }

    @Override
    public String toString() {
        return componentName + " " + modelName;
    }

}
