package Components;

import Enums.DiskSocket;
import Enums.DiskType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "primary_storage")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class PrimaryStorage extends Component {

    @Id
    private Long id;

    @Column(name = "disk_type")
    @Enumerated(EnumType.STRING)
    private DiskType diskType;

    private double speed;

    private double capacity;

    @Enumerated(EnumType.STRING)
    private DiskSocket socket;

    public PrimaryStorage(String modelName, DiskType diskType, DiskSocket socket, int capacity) {
        this.modelName = modelName;
        this.diskType = diskType;
        this.socket = socket;
        this.capacity = capacity;
    }

    protected PrimaryStorage() {}

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Motherboard) {
            return component.isCompatible(this);
        }
        return true;
    }

    @Override
    public String getComponentName() {
        return "Диск";
    }

}
