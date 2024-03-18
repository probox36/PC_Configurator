package Entities;

import Enums.Socket;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cpu")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class CPU extends Component {

    @Id
    private Long id;

    @Column(name = "compatible_socket")
    @Enumerated(EnumType.STRING)
    private Socket compatibleSocket;

    private int cores;

    private double frequency;

    @Column(name = "has_graphics_cores")
    private boolean hasGraphicsCore;

    @Column(name = "l1_cache")
    private int L1Cache;

    @Column(name = "l2_cache")
    private int L2Cache;

    @Column(name = "l3_cache")
    private int L3Cache;

    public CPU(String modelName, Socket compatibleSocket, int cores) {
        this.modelName = modelName;
        this.compatibleSocket = compatibleSocket;
        this.cores = cores;
    }

    protected CPU() {}

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Motherboard) {
            return component.isCompatible(this);
        }
        return !(component instanceof CoolingSystem)
                || ((CoolingSystem) component).getCompatibleSocket().equals(compatibleSocket);
    }

    @Override
    public String getComponentName() {
        return "Процессор";
    }

}
