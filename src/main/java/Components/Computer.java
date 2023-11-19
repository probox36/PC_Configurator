package Components;

import Enums.CaseCoolerSize;
import Enums.DiskSocket;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.*;

@Getter
@Entity
@Table(name = "computer")
public class Computer {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cooling_system_id")
    private CoolingSystem coolingSystem;

    @ManyToOne
    @JoinColumn(name = "cpu_id")
    private CPU cpu;

    @ManyToOne
    @JoinColumn(name = "gpu_id")
    private GPU gpu;

    @ManyToOne
    @JoinColumn(name = "motherboard_id")
    private Motherboard motherboard;

    @ManyToOne
    @JoinColumn(name = "power_unit_id")
    private PowerUnit powerUnit;

    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case cCase;

    @ElementCollection
    @CollectionTable(name = "computer_storage", joinColumns = {@JoinColumn(name = "computer_id")})
    private final List<PrimaryStorage> storage = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "computer_ram", joinColumns = {@JoinColumn(name = "computer_id")})
    private final List<RAM> ram = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "computer_coolers", joinColumns = {@JoinColumn(name = "computer_id")})
    private final List<CaseCooler> coolers = new ArrayList<>();


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "computer_case_cooler_mounts", joinColumns = {@JoinColumn(name = "computer_id")})
    @MapKeyColumn(name = "case_cooler_size")
    @Column(name = "mount_num")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<CaseCoolerSize, Integer> freeCaseCoolerMounts;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "computer_disk_connectors", joinColumns = {@JoinColumn(name = "computer_id")})
    @Column(name = "connector_num")
    @MapKeyColumn(name = "disk_socket")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<DiskSocket, Integer> freeDiskConnectors;

    @Column(name = "free_ram_slots")
    private int freeRAMSlots;

    @Column(name = "free_hdd_mounts")
    private int freeHDDMounts;

    @Transient
    CompatibilityChecker checker = new CompatibilityChecker();

    public List<Component> getComponents() {
        List<Component> components = new ArrayList<>(
                Arrays.asList(coolingSystem, cpu, gpu, motherboard, powerUnit, cCase));
        components.addAll(storage);
        components.addAll(ram);
        return components;
    }

    protected Computer() {}

    public Computer(Case cCase, Motherboard motherboard) {
        if (!checker.isCompatible(cCase, motherboard)) {
            throw new RuntimeException("Motherboard is incompatible with the case");
        }
        this.cCase = cCase;
        this.motherboard = motherboard;
        freeDiskConnectors = new HashMap<>(motherboard.getDiskConnectors());
        freeCaseCoolerMounts = new HashMap<>(cCase.getCaseCoolerMounts());
        freeRAMSlots = motherboard.getRAMSlots();
        freeHDDMounts = cCase.getHDDMounts();
    }

    public void setCoolingSystem(CoolingSystem coolingSystem) {
        if (checker.isCompatible(coolingSystem)) {
            this.coolingSystem = coolingSystem;
        } else {
            throw new RuntimeException(coolingSystem + " is incompatible with " + checker.getIncompatibility());
        }
    }

    public void setCpu(CPU cpu) {
        if (checker.isCompatible(cpu)) {
            this.cpu = cpu;
        } else {
            throw new RuntimeException(cpu + " is incompatible with " + checker.getIncompatibility());
        }
    }

    public void setGpu(GPU gpu) {
        if (checker.isCompatible(gpu)) {
            this.gpu = gpu;
        } else {
            throw new RuntimeException(gpu + " is incompatible with " + checker.getIncompatibility());
        }
    }

    public void setPowerUnit(PowerUnit powerUnit) {
        if (checker.isCompatible(powerUnit)) {
            this.powerUnit = powerUnit;
        } else {
            throw new RuntimeException(powerUnit + " is incompatible with " + checker.getIncompatibility());
        }
    }

    public void addDisk(PrimaryStorage disk) {
        if (!checker.isCompatible(disk)) {
            throw new RuntimeException(disk + " is incompatible with " + checker.getIncompatibility());
        }
        DiskSocket socket = disk.getSocket();
        Integer freeSlots = freeDiskConnectors.get(socket);
        if (freeSlots == null || freeSlots < 1) {
            throw new RuntimeException("Out of " + socket + " slots (tried to add " + disk + ")");
        }
        freeDiskConnectors.put(socket, freeSlots - 1);
        storage.add(disk);
    }

    public void addRAM(RAM ram) {
        if (!checker.isCompatible(ram)) {
            throw new RuntimeException(ram + " is incompatible with " + checker.getIncompatibility());
        }
        if (freeRAMSlots < 1) {
            throw new RuntimeException("Out of RAM slots (tried to add " + ram + ")");
        }
        freeRAMSlots--;
        this.ram.add(ram);
    }

    public void addCooler(CaseCooler cooler) {
        CaseCoolerSize size = cooler.getSize();
        Integer freeMounts = freeCaseCoolerMounts.get(size);

        if (freeMounts == null || freeMounts < 1) {
            throw new RuntimeException("Out of " + size + " cooler mounts (tried to add " + cooler + ")");
        }
        if (!checker.isCompatible(cooler)) {
            throw new RuntimeException(cooler + " is incompatible with " + checker.getIncompatibility());
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
            if (c != null && c.getPrice() != null) {
                consumption += c.getPowerConsumption();
            }
        }
        return consumption;
    }

    public double getTotalPrice() {
        double price = 0;
        for (Component c: getComponents()) {
            if (c != null && c.getPrice() != null) {
                price += c.getPrice();
            }
        }
        return price;
    }

    private class CompatibilityChecker {

        private Component incompatibility;

        public Component getIncompatibility() { return incompatibility; }

        public boolean isCompatible(Component component) {

            if (component instanceof CaseCooler || component instanceof Motherboard) {
                incompatibility = cCase;
                return cCase.isCompatible(component);
            }
            if (component instanceof CoolingSystem ||
                component instanceof CPU ||
                component instanceof GPU ||
                component instanceof RAM) {
                incompatibility = motherboard;
                    return motherboard.isCompatible(component);
            }
            if (component instanceof PrimaryStorage) {
                if (!cCase.isCompatible(component)) {
                    incompatibility = cCase;
                    return false;
                }
                if (!motherboard.isCompatible(component)) {
                    incompatibility = motherboard;
                    return false;
                }
                return true;
            }
            return true;

        }

        public boolean isCompatible(Component one, Component two) {
            return one.isCompatible(two);
        }
    }
}
