package Entities;

import Enums.CaseCoolerSize;
import Enums.DiskSocket;
import Exceptions.ComponentNotCompatibleException;
import Exceptions.ComputerNotInitializedException;
import Exceptions.NoSlotsLeftException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Entity
@Getter
@ToString
@Table(name = "computer")
public class Computer implements DBEntity {

    @JsonIgnore
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
    private Case computerCase;

    @JsonProperty("PrimaryStorage")
    @ElementCollection
    @CollectionTable(name = "computer_storage", joinColumns = {@JoinColumn(name = "computer_id")})
    private final List<PrimaryStorage> storage = new ArrayList<>();

    @JsonProperty("RAM")
    @ElementCollection
    @CollectionTable(name = "computer_ram", joinColumns = {@JoinColumn(name = "computer_id")})
    private final List<RAM> ram = new ArrayList<>();

    @JsonProperty("CaseCoolers")
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

    public List<Component> getComponents() {
        List<Component> components = new ArrayList<>(
                Arrays.asList(coolingSystem, cpu, gpu, motherboard, powerUnit, computerCase));
        components.addAll(storage);
        components.addAll(ram);
        return components;
    }

    protected Computer() {}

    public Computer(Case computerCase, Motherboard motherboard) {
        if (!computerCase.isCompatible(motherboard)) {
            throw new ComponentNotCompatibleException(motherboard + " is incompatible with " + computerCase);
        }
        this.computerCase = computerCase;
        this.motherboard = motherboard;
        freeDiskConnectors = new HashMap<>(motherboard.getDiskConnectors());
        freeCaseCoolerMounts = new HashMap<>(computerCase.getCaseCoolerMounts());
        freeRAMSlots = motherboard.getRamSlots();
        freeHDDMounts = computerCase.getHDDMounts();
    }

    public void removeDiskById(int index) {
        DiskSocket socket = storage.get(index).getSocket();
        storage.remove(index);
        int freeSlots = freeDiskConnectors.get(socket);
        freeDiskConnectors.put(socket, freeSlots - 1);
    }

    public void removeRAMById(int index) {
        ram.remove(index);
        freeRAMSlots++;
    }

    public void removeCoolerById(int index) {
        CaseCoolerSize size = coolers.get(index).getSize();
        coolers.remove(index);
        int freeMounts = freeCaseCoolerMounts.get(size);
        freeCaseCoolerMounts.put(size, freeMounts + 1);
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

    public void removePart(Component component) {
        switch (component.getClass().toString()) {
            case "class Entities.CoolingSystem" -> this.coolingSystem = null;
            case "class Entities.CPU" -> this.cpu = null;
            case "class Entities.GPU" -> this.gpu = null;
            case "class Entities.PowerUnit" -> this.powerUnit = (PowerUnit) component;
            case "class Entities.CaseCooler" -> removeCoolerById(coolers.indexOf((CaseCooler) component));
            case "class Entities.PrimaryStorage" -> removeDiskById(storage.indexOf((PrimaryStorage) component));
            case "class Entities.RAM" -> removeRAMById(ram.indexOf((RAM) component));
        }
    }

    public void addPart(Component component) {

        assertCompatible(component);

        switch (component.getClass().toString()) {
            case "class Entities.CoolingSystem" -> this.coolingSystem = (CoolingSystem) component;
            case "class Entities.CPU" -> this.cpu = (CPU) component;
            case "class Entities.GPU" -> this.gpu = (GPU) component;
            case "class Entities.PowerUnit" -> this.powerUnit = (PowerUnit) component;
            case "class Entities.CaseCooler" -> {

                CaseCooler cooler = (CaseCooler) component;
                CaseCoolerSize size = cooler.getSize();
                Integer freeMounts = freeCaseCoolerMounts.get(size);

                if (freeMounts == null || freeMounts < 1) {
                    throw new NoSlotsLeftException("Out of " + size + " cooler mounts (tried to add " + cooler + ")");
                }
                freeCaseCoolerMounts.put(size, freeMounts - 1);
                this.coolers.add(cooler);
            }

            case "class Entities.PrimaryStorage" -> {

                PrimaryStorage disk = (PrimaryStorage) component;
                DiskSocket socket = disk.getSocket();
                Integer freeSlots = freeDiskConnectors.get(socket);
                if (freeSlots == null || freeSlots < 1) {
                    throw new NoSlotsLeftException("Out of " + socket + " slots (tried to add " + disk + ")");
                }
                freeDiskConnectors.put(socket, freeSlots - 1);
                storage.add(disk);
            }

            case "class Entities.RAM" -> {
                RAM ram = (RAM) component;
                if (freeRAMSlots < 1) {
                    throw new NoSlotsLeftException("Out of RAM slots (tried to add " + ram + ")");
                }
                freeRAMSlots--;
                this.ram.add(ram);
            }
        }
    }

    public void assertCompatible(Component component) {
        CheckResult compatible = isCompatible(component);
        if (!compatible.toBoolean) {
            throw new ComponentNotCompatibleException(
                    component + " is incompatible with " + compatible.incompatibility
            );
        }
    }

    public record CheckResult(boolean toBoolean, Component incompatibility) {}

    public CheckResult isCompatible(Component component) throws ComputerNotInitializedException {

        if (computerCase == null || motherboard == null) {
            throw new ComputerNotInitializedException("Can't initialize computer with no case/motherboard");
        }

        if (component instanceof CaseCooler || component instanceof Motherboard) {
            return new CheckResult(computerCase.isCompatible(component), computerCase);
        }
        if (component instanceof CoolingSystem ||
                component instanceof Case ||
                component instanceof CPU ||
                component instanceof GPU ||
                component instanceof RAM) {
            return new CheckResult(motherboard.isCompatible(component), motherboard);
        }
        if (component instanceof PrimaryStorage) {
            if (!computerCase.isCompatible(component)) {
                return new CheckResult(false, computerCase);
            }
            if (!motherboard.isCompatible(component)) {
                return new CheckResult(false, motherboard);
            }
            return new CheckResult(true, null);
        }
        return new CheckResult(true, null);

    }

}
