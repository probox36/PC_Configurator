package Components;

import Enums.CaseCoolerSize;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaseCooler extends Component {

    private CaseCoolerSize size;

    public CaseCooler(String modelName, CaseCoolerSize size) {
        componentName = "Корпус";
        this.modelName = modelName;
        this.size = size;
    }


    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Case) {
            return component.isCompatible(this);
        }
        return true;
    }

    @Override
    public String toString() {
        return componentName + " " + modelName;
    }

}
