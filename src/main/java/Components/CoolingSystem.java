package Components;

import Enums.CoolingSystemType;
import Enums.Socket;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cooling_system")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class CoolingSystem extends Component {

    @Id
    private Long id;

    @Column(name = "compatible_socket")
    @Enumerated(EnumType.STRING)
    private Socket compatibleSocket;

    @Enumerated(EnumType.STRING)
    private CoolingSystemType type;

    public CoolingSystem(String modelName, Socket compatibleSocket, CoolingSystemType type) {
        this.modelName = modelName;
        this.compatibleSocket = compatibleSocket;
        this.type = type;
    }

    protected CoolingSystem() {}

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Motherboard) {
            return component.isCompatible(this);
        }
        return !(component instanceof CPU)
                || ((CPU) component).getCompatibleSocket().equals(compatibleSocket);
    }

    @Override
    public String getComponentName() {
        return "Система охлаждения";
    }

    @Override
    public String toString() {
        return getComponentName() + " " + modelName;
    }
}
