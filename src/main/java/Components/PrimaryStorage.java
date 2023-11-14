package Components;

import Enums.DiskSocket;
import Enums.DiskType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PrimaryStorage extends Component {

    private DiskType diskType;
    private double speed;
    private double capacity;
    private DiskSocket socket;

    public PrimaryStorage(String modelName) {
        componentName = "Диск";
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
