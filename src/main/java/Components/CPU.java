package Components;

import Enums.Socket;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CPU extends Component {

    private Socket compatibleSocket;
    private int cores;
    private double frequency;
    private boolean hasGraphicsCore;
    private int L1Cache;
    private int L2Cache;
    private int L3Cache;

    public CPU(String modelName, Socket compatibleSocket, int cores) {
        componentName = "Процессор";
        this.modelName = modelName;
        this.compatibleSocket = compatibleSocket;
        this.cores = cores;
    }

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Motherboard) {
            return component.isCompatible(this);
        }
        return !(component instanceof CoolingSystem)
                || ((CoolingSystem) component).getCompatibleSocket().equals(compatibleSocket);
    }

    @Override
    public String toString() {
        return componentName + " " + modelName;
    }

}
