package Components;

import Enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "cases")
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class Case extends Component {

    @Column(name = "motherboard_size")
    @Enumerated(EnumType.STRING)
    private MotherboardSize motherboardSize;

    @Column(name = "power_unit_size")
    @Enumerated(EnumType.STRING)
    private PowerUnitSize powerUnitSize;

    @ElementCollection
    @CollectionTable(name = "case_cooler_mounts", joinColumns = {@JoinColumn(name = "case_id")})
    @MapKeyColumn(name = "cooler_size")
    @Column(name = "mount_num")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<CaseCoolerSize, Integer> caseCoolerMounts;

    @Column(name = "hdd_mounts")
    private int HDDMounts;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    @CollectionTable(name = "case_features", joinColumns = @JoinColumn(name = "case_id"))
    private List<CaseFeature> features;

    public Case(String modelName, MotherboardSize motherboardSize, PowerUnitSize powerUnitSize, int HDDMounts,
                Map<CaseCoolerSize, Integer> caseCoolerMounts) {
        this.motherboardSize = motherboardSize;
        this.powerUnitSize = powerUnitSize;
        this.modelName = modelName;
        this.HDDMounts = HDDMounts;
        this.caseCoolerMounts = caseCoolerMounts;
    }

    protected Case() {}

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Motherboard) {
            return component.isCompatible(this);
        }
        if (component instanceof CaseCooler) {
            CaseCoolerSize size = ((CaseCooler) component).getSize();
            Integer freeMounts = caseCoolerMounts.get(size);
            if (freeMounts == null || freeMounts < 1) {
                return false;
            }
        }
        return !(component instanceof PrimaryStorage) || !((PrimaryStorage) component).getDiskType().equals(DiskType.HDD)
                || getHDDMounts() >= 1;
    }

    @Override
    public String getComponentName() {
        return "Корпус";
    }

    @Override
    public String toString() {
        return getComponentName() + " " + modelName;
    }

}
