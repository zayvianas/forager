package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ForagerRepository;
import learn.foraging.data.ForagerRepositoryDouble;
import learn.foraging.models.Forager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ForagerServiceTest {

    private ForagerService service;


    @BeforeEach
    void setup() {
        ForagerRepository repository = new ForagerRepositoryDouble();
        service = new ForagerService(repository);
    }

    @Test
    void shouldNotFindLastNamesThatDoNotExist() {
        List<Forager> listOfForagers = service.findByLastName("asdf");

        assertEquals(0, listOfForagers.size());
    }

    @Test
    void shouldFindLastName() throws DataException {
        List<Forager> foragerList = service.findByLastName("s");

        assertTrue(foragerList.size() == 1);
    }

    @Test
    void shouldNotFindForStatesNotInRepo() {
        List<Forager> foragerList = service.findByState("NY");

        assertTrue(foragerList.size() == 0);
    }

    @Test
    void shouldFindOneStateInRepo() {
        List<Forager> foragerList = service.findByState("GA");

        assertTrue(foragerList.size() == 1);
    }

    @Test
    void shouldAdd() throws DataException {
        Forager newForager = new Forager();
        newForager.setFirstName("TestFirstName");
        newForager.setLastName("TestLastName");
        newForager.setState("NY");
        newForager.setId("asdf-wqer-asdf");

        Result<Forager> foragerResult = service.addForager(newForager);

        assertTrue(foragerResult.isSuccess());
        assertEquals("TestFirstName", foragerResult.getPayload().getFirstName());
        assertEquals("TestLastName", foragerResult.getPayload().getLastName());
        assertEquals("NY", foragerResult.getPayload().getState());
    }

    @Test
    void shouldNotAddNullForager() throws DataException {
        Forager newForager = null;

        Result<Forager> foragerResult = service.addForager(newForager);

        assertFalse(foragerResult.isSuccess());
        assertEquals("Forager cannot be null.", foragerResult.getErrorMessages().get(0));
    }

    @Test
    void shouldNotAddWithNoFirstName() throws DataException {
        Forager newForager = new Forager();
        newForager.setFirstName("");
        newForager.setLastName("TestLastName");
        newForager.setState("NY");
        newForager.setId("asdf-wqer-asdf");

        Result<Forager> foragerResult = service.addForager(newForager);

        assertFalse(foragerResult.isSuccess());
        assertEquals("Forager first name is required", foragerResult.getErrorMessages().get(0));
    }

    @Test
    void shouldNotAddWithNoLastName() throws DataException {
        Forager newForager = new Forager();
        newForager.setFirstName("TestFirstName");
        newForager.setLastName("");
        newForager.setState("NY");
        newForager.setId("asdf-wqer-asdf");

        Result<Forager> foragerResult = service.addForager(newForager);

        assertFalse(foragerResult.isSuccess());
        assertEquals("Forager last name is required", foragerResult.getErrorMessages().get(0));
    }

    @Test
    void shouldNotAddWithNoState() throws DataException {
        Forager newForager = new Forager();
        newForager.setFirstName("TestFirstName");
        newForager.setLastName("TestLastName");
        newForager.setState("");
        newForager.setId("asdf-wqer-asdf");

        Result<Forager> foragerResult = service.addForager(newForager);

        assertFalse(foragerResult.isSuccess());
        assertEquals("State is required", foragerResult.getErrorMessages().get(0));
    }
}
