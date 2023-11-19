package Components;

import Enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.Map;

@Entity
@Table(name = "motherboard")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Motherboard extends Component {

    @Id
    private Long id;

    @Column(name = "compatible_socket")
    @Enumerated(EnumType.STRING)
    private Socket compatibleSocket;

    @Column(name = "ram_slots")
    private int RAMSlots;

    @Column(name = "ram_generation")
    @Enumerated(EnumType.STRING)
    private RAMGeneration ramGeneration;

    @Column(name = "motherboard_size")
    @Enumerated(EnumType.STRING)
    private MotherboardSize size;

    @Column(name = "max_cores")
    private int maxCores;

    @Column(name = "max_memory")
    private int maxMemory;

    @ElementCollection
    @CollectionTable(name = "motherboard_disk_connectors", joinColumns = {@JoinColumn(name = "motherboard_id")})
    @MapKeyColumn(name = "disk_socket")
    @Column(name = "socket_num")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<DiskSocket, Integer> diskConnectors;

    public Motherboard(String modelName, Socket compatibleSocket, int RAMSlots, RAMGeneration ramGeneration,
                       MotherboardSize size, int maxCores, int maxMemory, Map<DiskSocket, Integer> diskSockets) {
        this.modelName = modelName;
        this.compatibleSocket = compatibleSocket;
        this.RAMSlots = RAMSlots;
        this.ramGeneration = ramGeneration;
        this.size = size;
        this.maxCores = maxCores;
        this.maxMemory = maxMemory;
        this.diskConnectors = diskSockets;
    }

    protected Motherboard() {}

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof CPU) {
            if (!((CPU) component).getCompatibleSocket().equals(compatibleSocket)
                || ((CPU) component).getCores() > maxCores) {
                return false;
            }
        }
        if (component instanceof CoolingSystem
                && !((CoolingSystem) component).getCompatibleSocket().equals(compatibleSocket)) {
            return false;
        }
        if (component instanceof PrimaryStorage) {
            DiskSocket socket = ((PrimaryStorage) component).getSocket();
            Integer freeConnectors = diskConnectors.get(socket);
            if (freeConnectors == null || freeConnectors < 1) {
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
    public String getComponentName() {
        return "Материнская плата";
    }

    @Override
    public String toString() {
        return getComponentName() + " " + modelName;
    }

}
