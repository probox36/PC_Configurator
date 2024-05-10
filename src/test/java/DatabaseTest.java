import Database.Database;
import Database.HibernateConfigurator;
import Entities.*;
import Enums.*;
import Exceptions.ComponentNotCompatibleException;
import Exceptions.NoSlotsLeftException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.HashMap;
import java.util.Map;

public class DatabaseTest {

    private static Database db;
    private static Session session;

    @BeforeClass
    public static void init() {
        db = new Database();
        session = HibernateConfigurator.getSession();
    }

    @Before
    public void beforeEach() {

    }

    @Test
    public void addEntityTest() {

        // протестируем связь с базой данных: проверим, что что можно успешно добавить компонент в БД
        // и получить его же обратно

        Transaction transaction = session.beginTransaction();
        CPU expected = new CPU("Intel Core i8", Socket.LGA1700, 6);
        session.persist(expected);
        transaction.commit();

        CPU actual = db.performQuery("from CPU where modelName = 'Intel Core i8'", CPU.class).get(0);

        Assert.assertEquals(actual.getModelName(), expected.getModelName());
        Assert.assertEquals(actual.getComponentName(), expected.getComponentName());
        Assert.assertEquals(actual.getCompatibleSocket(), expected.getCompatibleSocket());
        Assert.assertEquals(actual.getCores(), expected.getCores());

        transaction = session.beginTransaction();
        session.evict(expected);
        session.remove(actual);
        transaction.commit();
    }

    @Test
    public void partAddingTest() {
        Computer computer = new Computer(getCase(), getMotherboard());
        int expected = computer.getFreeRAMSlots();
        RAM ram = new RAM("Corsair awd789", RAMGeneration.DDR4, 8.0, 2400);

        // добавим в объект компьютера планки оперативной памяти, причем причем попытаемся добавить на одну больше, чем
        // есть свободных слотов. Таким образом проверим, корректно ли работает метод добавления компонентов и возникает
        // ли ошибка NoSlotsLeftException при попытке добавить больше деталей, чем возможно

        ThrowingRunnable runnable = () -> {
            for (int i = 0; i < (expected + 1); i++) { computer.addPart(ram); }
        };

        Assert.assertThrows(NoSlotsLeftException.class, runnable);
        Assert.assertEquals(computer.getRam().get(0), ram);
        Assert.assertEquals(computer.getFreeRAMSlots(), 0);

        // уберем все планки по очереди и проверим, что в соответствующем массиве ничего не осталось

        for (int i = 0; i < 4; i++) { computer.removePart(ram); }
        Assert.assertEquals(0, computer.getRam().size());
        Assert.assertEquals(expected, computer.getFreeRAMSlots());
    }

    @Test
    public void compatibilityTest() {
        // попытаемся создать объект компьютера на основе miniATX корпуса и ATX матплпаты. Убедимся, что это
        // вызывает ComponentNotCompatibleException
        ThrowingRunnable runnable = () -> { Computer c = new Computer(getSmallerCase(), getMotherboard()); };
        Assert.assertThrows(ComponentNotCompatibleException.class, runnable);

        RAM ramDDR5 = new RAM("Corsair awd889", RAMGeneration.DDR5, 8.0, 3600);
        RAM ramDDR4 = new RAM("Corsair awd789", RAMGeneration.DDR4, 8.0, 2400);

        // попытаемся добавить к компьютеру, поддерживающему только DDR4 память планку DDR5 ОЗУ. Убедимся, что это
        // вызывает ComponentNotCompatibleException
        Computer computer  = new Computer(getCase(), getMotherboard());
        ThrowingRunnable runnable2 = () -> computer.addPart(ramDDR5);
        Assert.assertThrows(ComponentNotCompatibleException.class, runnable2);

        // протестируем метод isCompatible: проверим совместимую и несовместимую планки ОЗУ, убедимся в том, что
        // результат проверки правильно указывает на несовместимый компонент

        Assert.assertTrue(computer.isCompatible(ramDDR4).toBoolean());
        Computer.CheckResult result = computer.isCompatible(ramDDR5);
        Assert.assertFalse(result.toBoolean());
        Assert.assertEquals(result.incompatibility().getComponentName(), getMotherboard().getComponentName());
    }

    private static Case getCase() {
        Map<CaseCoolerSize, Integer> coolerMounts = new HashMap<>();
        coolerMounts.put(CaseCoolerSize.mm120x120, 2);
        coolerMounts.put(CaseCoolerSize.mm140x140, 1);
        return new Case("InWin wdn-12", MotherboardSize.ATX, PowerUnitSize.ATX,
                2, coolerMounts);
    }

    private static Case getSmallerCase() {
        Map<CaseCoolerSize, Integer> coolerMounts = new HashMap<>();
        coolerMounts.put(CaseCoolerSize.mm120x120, 1);
        coolerMounts.put(CaseCoolerSize.mm140x140, 1);
        return new Case("Montech air100", MotherboardSize.Mini_ATX, PowerUnitSize.ATX,
                2, coolerMounts);
    }

    private static Motherboard getMotherboard() {
        Map<DiskSocket, Integer> diskSockets = new HashMap<>();
        diskSockets.put(DiskSocket.SATA, 2);
        diskSockets.put(DiskSocket.M_2, 2);
        return new Motherboard("ASUS koi-8r", Socket.AM4, 4, RAMGeneration.DDR4,
                MotherboardSize.ATX, 16, diskSockets);
    }

}
