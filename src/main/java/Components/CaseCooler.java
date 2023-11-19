package Components;

import Enums.CaseCoolerSize;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "case_cooler")
@Getter
@Setter
public class CaseCooler extends Component {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private CaseCoolerSize size;

    public CaseCooler(String modelName, CaseCoolerSize size) {
        this.modelName = modelName;
        this.size = size;
    }

    protected CaseCooler() {}

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Case) {
            return component.isCompatible(this);
        }
        return true;
    }

    @Override
    public String getComponentName() {
        return "Кулер";
    }

    @Override
    public String toString() {
        return getComponentName() + " " + modelName;
    }

}
