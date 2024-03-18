package Entities;

import Enums.PowerUnitSize;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "power_unit")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class PowerUnit extends Component {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private PowerUnitSize size;

    public PowerUnit(String modelName, PowerUnitSize size, int power) {
        this.modelName = modelName;
        this.size = size;
        this.powerConsumption = power * -1;
    }

protected PowerUnit() {}

    @Override
    public boolean isCompatible(Component component) {
        return true;
    }

    @Override
    public String getComponentName() {
        return "Блок питания";
    }

    public double getPower() { return powerConsumption * -1; }

}
