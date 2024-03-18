package Entities;

import Database.HibernateConfigurator;
import ExceptionClasses.ComponentNotCompatibleException;
import ExceptionClasses.ComputerNotInitializedException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.Session;

@Data
public class ComputerJsonModel {

    @JsonProperty("Motherboard")
    private Integer motherboardId;
    @JsonProperty("Case")
    private Integer caseId;

    public Computer convertToComputer() throws ComputerNotInitializedException, ComponentNotCompatibleException {

        if (caseId == null && motherboardId == null) {
            throw new RuntimeException("Can't initialize computer without case or motherboard");
        }

        Session session = HibernateConfigurator.getSession();
        Motherboard motherboard = session.get(Motherboard.class, motherboardId);
        Case computerCase = session.get(Case.class, caseId);

        return new Computer(computerCase, motherboard);

    }

}
