package Components;

import Enums.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@Builder(builderMethodName = "requiredBuilder")
@EqualsAndHashCode(callSuper = true)
@Data
public class Motherboard extends Component {

    private String componentName;
    private String modelName;
    private Socket compatibleSocket;
    private int RAMSlots;
    private RAMGeneration ramGeneration;
    private MotherboardSize size;
    private HashMap<DiskSocket, Integer> diskConnectors;

    public Motherboard(String modelName, Socket compatibleSocket, int RAMSlots, RAMGeneration ramGeneration,
                       MotherboardSize size, HashMap<DiskSocket, Integer> diskSockests) {
        this.modelName = modelName;
        this.compatibleSocket = compatibleSocket;
        this.RAMSlots = RAMSlots;
        this.ramGeneration = ramGeneration;
        this.size = size;
        this.diskConnectors = diskSockests;
    }

    public static MotherboardBuilder builder(String modelName, Socket compatibleSocket, int RAMSlots,
        RAMGeneration ramGeneration, MotherboardSize size, HashMap<DiskSocket, Integer> diskSockests) {
        return requiredBuilder().componentName("Материнская плата").modelName(modelName).compatibleSocket(compatibleSocket)
                .RAMSlots(RAMSlots).ramGeneration(ramGeneration).size(size).diskConnectors(diskSockests);
    }

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof CPU
                && !((CPU) component).getCompatibleSocket().equals(compatibleSocket)) {
            return false;
        }
        if (component instanceof CoolingSystem
                && !((CoolingSystem) component).getCompatibleSocket().equals(compatibleSocket)) {
            return false;
        }
        if (component instanceof PrimaryStorage) {
            DiskSocket socket = ((PrimaryStorage) component).getSocket();
            if (diskConnectors.get(socket) < 1) {
                return false;
            }
        }
        if (component instanceof Case && !((Case) component).getMotherboardSize().equals(this.size)) {
            return false;
        }
        return !(component instanceof RAM)
                || ((RAM) component).getRamGeneration().equals(ramGeneration);
    }

    @Override
    public String toString() {
        return componentName + " " + modelName;
    }

}
