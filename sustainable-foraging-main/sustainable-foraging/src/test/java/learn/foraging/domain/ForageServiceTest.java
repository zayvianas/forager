package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ForageRepositoryDouble;
import learn.foraging.data.ForagerRepositoryDouble;
import learn.foraging.data.ItemRepositoryDouble;
import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ForageServiceTest {

    ForageService service = new ForageService(
            new ForageRepositoryDouble(),
            new ForagerRepositoryDouble(),
            new ItemRepositoryDouble());

    @Test
    void shouldAdd() throws DataException {
        Forage forage = new Forage();
        forage.setDate(LocalDate.now());
        forage.setForager(ForagerRepositoryDouble.FORAGER);
        forage.setItem(ItemRepositoryDouble.ITEM);
        forage.setKilograms(0.5);

        Result<Forage> result = service.add(forage);
        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
        assertEquals(36, result.getPayload().getId().length());
    }

    @Test
    void shouldNotAddWhenForagerNotFound() throws DataException {

        Forager forager = new Forager();
        forager.setId("30816379-188d-4552-913f-9a48405e8c08");
        forager.setFirstName("Ermengarde");
        forager.setLastName("Sansom");
        forager.setState("NM");

        Forage forage = new Forage();
        forage.setDate(LocalDate.now());
        forage.setForager(forager);
        forage.setItem(ItemRepositoryDouble.ITEM);
        forage.setKilograms(0.5);

        Result<Forage> result = service.add(forage);
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldNotAddWhenItemNotFound() throws DataException {

        Item item = new Item(11, "Dandelion", Category.EDIBLE, new BigDecimal("0.05"));

        Forage forage = new Forage();
        forage.setDate(LocalDate.now());
        forage.setForager(ForagerRepositoryDouble.FORAGER);
        forage.setItem(item);
        forage.setKilograms(0.5);

        Result<Forage> result = service.add(forage);
        assertFalse(result.isSuccess());
    }


    // TODO These are all of the new tests that I've added.

    @Test
    void shouldNotAddNullForage() throws DataException {

        Forage forage = null;

        Result<Forage> result = service.add(forage);
        assertEquals("Forage is null", result.getErrorMessages().get(0));
    }


    @Test
    void shouldNotAddWhenForageInFuture() throws DataException {
        LocalDate date = LocalDate.of(2024, 12, 12);

        Forage forage = new Forage();
        forage.setDate(date);
        forage.setForager(ForagerRepositoryDouble.FORAGER);
        forage.setItem(ItemRepositoryDouble.ITEM);
        forage.setKilograms(0.5);

        Result<Forage> result = service.add(forage);
        assertFalse(result.isSuccess());
        assertEquals("Forage date cannot be in the future", result.getErrorMessages().get(0));

    }

    @Test
    void shouldNotAddIfKgIsZero() throws DataException {

            Forage forage = new Forage();
            forage.setDate(LocalDate.now());
            forage.setForager(ForagerRepositoryDouble.FORAGER);
            forage.setItem(ItemRepositoryDouble.ITEM);
            forage.setKilograms(0.0);

            Result<Forage> result = service.add(forage);

            assertFalse(result.isSuccess());
            assertEquals("Kilograms must be a positive number less than 250.0", result.getErrorMessages().get(0));
        }

    @Test
    void shouldNotAddIfKgIsGreaterThan250() throws DataException {

        Forage forage = new Forage();
        forage.setDate(LocalDate.now());
        forage.setForager(ForagerRepositoryDouble.FORAGER);
        forage.setItem(ItemRepositoryDouble.ITEM);
        forage.setKilograms(250.1);

        Result<Forage> result = service.add(forage);

        assertFalse(result.isSuccess());
        assertEquals("Kilograms must be a positive number less than 250.0", result.getErrorMessages().get(0));
    }

}
