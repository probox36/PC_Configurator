package Components;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "gpu")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class GPU extends Component {

    @Id
    private Long id;

    @Column(name = "graphical_processor")
    private String graphicalProcessor;

    private double memory;

    public GPU(String modelName) {
        this.modelName = modelName;
    }

    protected GPU() {}

    @Override
    public boolean isCompatible(Component component) {
        return true;
    }

    @Override
    public String getComponentName() {
        return "Видеокарта";
    }

}
