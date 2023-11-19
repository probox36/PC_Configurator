package Components;

import Enums.RAMGeneration;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ram")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class RAM extends Component {

    @Id
    private Long id;

    @Column(name = "ram_generation")
    @Enumerated(EnumType.STRING)
    private RAMGeneration ramGeneration;

    private double frequency;

    private double capacity;

    public RAM(String modelName, RAMGeneration generation, double capacity) {
        this.modelName = modelName;
        this.ramGeneration = generation;
        this.capacity = capacity;
    }

    protected RAM() {}

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Motherboard) {
            return component.isCompatible(this);
        }
        return true;
    }

    @Override
    public String getComponentName() {
        return "Оперативная память";
    }

}
