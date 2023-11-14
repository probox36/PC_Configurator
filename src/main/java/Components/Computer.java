package Components;

import Enums.CaseCoolerSize;
import Enums.DiskSocket;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Builder
@Getter
public class Computer {

    
    private CoolingSystem coolingSystem;
    private CPU cpu;
    private GPU gpu;
    private final Motherboard motherboard;
    private PowerUnit powerUnit;
    private Case cCase;
    
    private final ArrayList<PrimaryStorage> storage = new ArrayList<>();
    private final ArrayList<RAM> ram = new ArrayList<>();
    private final ArrayList<CaseCooler> coolers = new ArrayList<>();

    private HashMap<CaseCoolerSize, Integer> freeCaseCoolerMounts;
    private HashMap<DiskSocket, Integer> freeDiskConnectors;

    private int freeRAMSlots;
    private int freeHDDSlots;

    CompatibilityChecker checker = new CompatibilityChecker();

    public ArrayList<Component> getComponents() {
        ArrayList<Component> components = new ArrayList<>(
                Arrays.asList(coolingSystem, cpu, gpu, motherboard, powerUnit, cCase));
        components.addAll(storage);
        components.addAll(ram);
        return components;
    }

    public Computer(Case cCase, Motherboard motherboard) {
        this.motherboard = motherboard;
        freeDiskConnectors = motherboard.getDiskConnectors();
        freeRAMSlots = motherboard.getRAMSlots();
        freeHDDSlots = cCase.getHDDMounts();
    }

    public void setCoolingSystem(CoolingSystem coolingSystem) {
        if (checker.isCompatible(coolingSystem)) {
            this.coolingSystem = coolingSystem;
        } else {
            throw new RuntimeException("Component is incompatible with " + checker.getIncompatibility());
        }
    }

    public void setCpu(CPU cpu) {
        if (checker.isCompatible(cpu)) {
            this.cpu = cpu;
        } else {
            throw new RuntimeException("Component is incompatible with " + checker.getIncompatibility());
        }
    }

    public void setGpu(GPU gpu) {
        if (checker.isCompatible(gpu)) {
            this.gpu = gpu;
        } else {
            throw new RuntimeException("Component is incompatible with " + checker.getIncompatibility());
        }
    }

    public void setPowerUnit(PowerUnit powerUnit) {
        if (checker.isCompatible(powerUnit)) {
            this.powerUnit = powerUnit;
        } else {
            throw new RuntimeException("Component is incompatible with " + checker.getIncompatibility());
        }
    }

    public void addDisk(PrimaryStorage disk) {
        if (!checker.isCompatible(disk)) {
            throw new RuntimeException("Component is incompatible with " + checker.getIncompatibility());
        }
        DiskSocket socket = disk.getSocket();
        int freeSlots = freeDiskConnectors.get(socket);
        if (freeSlots < 1) {
            throw new RuntimeException("Out of " + socket + " slots (tried to add " + disk + ")");
        }
        freeDiskConnectors.put(socket, freeSlots - 1);
        storage.add(disk);
    }

    public void addRAM(RAM ram) {
        if (!checker.isCompatible(ram)) {
            throw new RuntimeException("Component is incompatible with " + checker.getIncompatibility());
        }
        if (freeRAMSlots < 1) {
            throw new RuntimeException("Out of RAM slots (tried to add " + ram + ")");
        }
        freeRAMSlots--;
        this.ram.add(ram);
    }

    public void addCooler(CaseCooler cooler) throws Exception {
        CaseCoolerSize size = cooler.getSize();
        int freeMounts = freeCaseCoolerMounts.get(size);

        if (freeMounts < 1) {
            throw new RuntimeException("Out of case" + size + "cooler mounts (tried to add " + cooler + ")");
        }
        if (!checker.isCompatible(cooler)) {
            throw new RuntimeException("Component is incompatible with " + checker.getIncompatibility());
        }
        freeCaseCoolerMounts.put(size, freeMounts - 1);
        this.coolers.add(cooler);
    }

    public void removeDisk(int index) {
        DiskSocket socket = storage.get(index).getSocket();
        storage.remove(index);
        int freeSlots = freeDiskConnectors.get(socket);
        freeDiskConnectors.put(socket, freeSlots - 1);
    }

    public void removeDisk(PrimaryStorage disk) {
        DiskSocket socket = disk.getSocket();
        storage.remove(disk);
        int freeSlots = freeDiskConnectors.get(socket);
        freeDiskConnectors.put(socket, freeSlots - 1);
    }

    public void removeRAM(int index) {
        ram.remove(index);
    }

    public void removeRAM(RAM ram) {
        this.ram.remove(ram);
    }

    public void removeCooler(CaseCooler cooler) {
        this.coolers.remove(cooler);
        int freeMounts = freeCaseCoolerMounts.get(cooler.getSize());
        freeCaseCoolerMounts.put(cooler.getSize(), freeMounts + 1);
    }

    public double getPowerConsumption() {
        double consumption = 0;
        for (Component c: getComponents()) {
            if (c != null) {
                consumption += c.getPowerConsumption();
            }
        }
        return consumption;
    }

    public double getTotalPrice() {
        double price = 0;
        for (Component c: getComponents()) {
            if (c != null) {
                price += c.getPrice();
            }
        }
        return price;
    }

    private class CompatibilityChecker {

        private Component incompatibility;

        public Component getIncompatibility() { return incompatibility; }

        public boolean isCompatible(Component component) {
            ArrayList<Component> components = getComponents();
            for (Component c: components) {
                if (c != null && !c.isCompatible(component)) {
                    incompatibility = c;
                    return false;
                }
            }
            return true;
        }
    }

}
