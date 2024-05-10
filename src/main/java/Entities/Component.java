package Entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Component implements DBEntity {

    // hibernate хочет сквозную нумерацию для всех потомков component, но mysql не поддерживает sequence,
    // поэтому выкручиваемся таким образом
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "table-generator")
    @TableGenerator(name = "table-generator",
            table = "component_ids",
            pkColumnName = "sequence_id",
            valueColumnName = "sequence_value",
            initialValue = 1,
            allocationSize = 5)
    private Long id;


    @Column(name = "model_name")
    String modelName;

    Double price;

    String description;

    @Column(name = "img_address")
    private String imgAddress;

    @Column(name = "power_consumption")
    double powerConsumption;

    public abstract boolean isCompatible(Component component);

    public abstract String getComponentName();

    public String toString() {
        return getComponentName() + " " + modelName;
    }
}
