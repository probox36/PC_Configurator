package Components;

import Enums.PowerUnitSize;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PowerUnit extends Component {

    private double power;
    private PowerUnitSize size;

    public PowerUnit(String modelName) {
        componentName = "Блок питания";
        this.modelName = modelName;
    }


    @Override
    boolean isCompatible(Component component) {
        return false;
    }

    @Override
    public String toString() {
        return componentName + " " + modelName;
    }
}
