package Components;

import Enums.CoolingSystemType;
import Enums.Socket;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CoolingSystem extends Component {

    private Socket compatibleSocket;
    private CoolingSystemType type;

    public CoolingSystem(String modelName, Socket compatibleSocket) {
        componentName = "Система охлаждения";
        this.modelName = modelName;
        this.compatibleSocket = compatibleSocket;
    }

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Motherboard) {
            return component.isCompatible(this);
        }
        return !(component instanceof CPU)
                || ((CPU) component).getCompatibleSocket().equals(compatibleSocket);
    }

    @Override
    public String toString() {
        return componentName + " " + modelName;
    }
}
