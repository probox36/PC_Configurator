package Entities;

import Database.HibernateConfigurator;
import Exceptions.ComponentNotCompatibleException;
import Exceptions.ComputerNotInitializedException;
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
            throw new ComputerNotInitializedException("Can't initialize computer without case or motherboard");
        }

        Session session = HibernateConfigurator.getSession();
        Motherboard motherboard = session.get(Motherboard.class, motherboardId);
        Case computerCase = session.get(Case.class, caseId);

        Computer computer = new Computer(computerCase, motherboard);
        session.close();
        return computer;

    }

}
