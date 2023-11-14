package Components;

import Enums.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;

import java.util.ArrayList;
import java.util.HashMap;

@Builder(builderMethodName = "requiredBuilder")
@EqualsAndHashCode(callSuper = true)
@Data
public class Case extends Component {

    private String componentName;
    private String modelName;
    private MotherboardSize motherboardSize;
    private PowerUnitSize powerUnitSize;
    private HashMap<CaseCoolerSize, Integer> caseCoolerMounts;
    private int HDDMounts;
    private ArrayList<CaseFeature> features;

    public Case(String modelName, MotherboardSize motherboardSize, PowerUnitSize powerUnitSize, int HDDMounts,
                HashMap<CaseCoolerSize, Integer> caseCoolerMounts) {
        componentName = "Корпус";
        this.motherboardSize = motherboardSize;
        this.powerUnitSize = powerUnitSize;
        this.modelName = modelName;
        this.caseCoolerMounts = caseCoolerMounts;
    }

    public static CaseBuilder builder(String modelName, MotherboardSize motherboardSize, PowerUnitSize powerUnitSize,
                                      int HDDMounts, HashMap<CaseCoolerSize, Integer> caseCoolerMounts) {
        return requiredBuilder().componentName("Корпус").modelName(modelName).motherboardSize(motherboardSize)
                .powerUnitSize(powerUnitSize).HDDMounts(HDDMounts).caseCoolerMounts(caseCoolerMounts);
    }

    @Override
    public boolean isCompatible(Component component) {
        if (component instanceof Motherboard) {
            return component.isCompatible(this);
        }
        if (component instanceof CaseCooler) {
            CaseCoolerSize size = ((CaseCooler) component).getSize();
            if (caseCoolerMounts.get(size) < 1) {
                return false;
            }
        }
        return !(component instanceof PrimaryStorage) || !((PrimaryStorage) component).getDiskType().equals(DiskType.HDD)
                || getHDDMounts() >= 1;
    }

    @Override
    public String toString() {
        return componentName + " " + modelName;
    }

}
