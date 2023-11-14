package Components;

import Enums.Socket;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GPU extends Component {

    private String graphicalProcessor;
    private double memory;

    public GPU(String modelName) {
        componentName = "Видеокарта";
        this.modelName = modelName;
    }


    @Override
    boolean isCompatible(Component component) {
        return true;
    }

    @Override
    public String toString() {
        return componentName + " " + modelName;
    }
}
