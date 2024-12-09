package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ItemRepositoryDouble;
import learn.foraging.models.Category;
import learn.foraging.models.Item;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {

    ItemService service = new ItemService(new ItemRepositoryDouble());

    @Test
    void shouldNotSaveNullName() throws DataException {
        Item item = new Item(0, null, Category.EDIBLE, new BigDecimal("5.00"));
        Result<Item> result = service.add(item);
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldNotSaveBlankName() throws DataException {
        Item item = new Item(0, "   \t\n", Category.EDIBLE, new BigDecimal("5.00"));
        Result<Item> result = service.add(item);
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldNotSaveNullDollars() throws DataException {
        Item item = new Item(0, "Test Item", Category.EDIBLE, null);
        Result<Item> result = service.add(item);
        assertFalse(result.isSuccess());
        assertEquals("Kilograms cannot be null", result.getErrorMessages().get(0));
    }

    @Test
    void shouldNotSaveNegativeDollars() throws DataException {
        Item item = new Item(0, "Test Item", Category.EDIBLE, new BigDecimal("-5.00"));
        Result<Item> result = service.add(item);
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldNotSaveTooLargeDollars() throws DataException {
        Item item = new Item(0, "Test Item", Category.EDIBLE, new BigDecimal("9999.00"));
        Result<Item> result = service.add(item);
        assertFalse(result.isSuccess());
    }

    @Test
    void shouldSave() throws DataException {
        Item item = new Item(0, "Test Item", Category.EDIBLE, new BigDecimal("5.00"));

        Result<Item> result = service.add(item);

        assertNotNull(result.getPayload());
        assertEquals(2, result.getPayload().getId());
    }

    @Test
    void shouldNotAddZeroValueEdibleItem() throws DataException {
        Item item = new Item();
        item.setName("TestEdibleItem");
        item.setCategory(Category.EDIBLE);
        item.setDollarPerKilogram(BigDecimal.ZERO);
        Result<Item>  result =  service.add(item);

        assertFalse(result.isSuccess());
        assertEquals("$/Kg must be between 0.00 and 7500.00.", result.getErrorMessages().get(0));
    }

    @Test
    void voidShouldAddZeroValueForPoisonousItem() throws DataException {
        Item item = new Item();
        item.setName("TestEdibleItem");
        item.setCategory(Category.POISONOUS);
        item.setDollarPerKilogram(BigDecimal.ZERO);
        Result<Item>  result =  service.add(item);

        assertTrue(result.isSuccess());
        assertEquals(Category.POISONOUS, result.getPayload().getCategory());
    }
}