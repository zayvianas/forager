package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ItemRepository;
import learn.foraging.models.Category;
import learn.foraging.models.Item;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public List<Item> findByCategory(Category category) {
        return repository.findAll().stream()
                .filter(i -> i.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Result<Item> add(Item item) throws DataException {

        Result<Item> result = new Result<>();
        if (item == null) {
            result.addErrorMessage("Item must not be null.");
            return result;
        }

        if (item.getName() == null || item.getName().isBlank()) {
            result.addErrorMessage("Item name is required.");
        } else if (repository.findAll().stream()
                .anyMatch(i -> i.getName().equalsIgnoreCase(item.getName()))) {
            result.addErrorMessage(String.format("Item '%s' is a duplicate.", item.getName()));
        }

        if (item.getDollarPerKilogram() == null) {
            result.addErrorMessage("Kilograms cannot be null");
            return result;
        }

        if (item.getDollarPerKilogram().compareTo(BigDecimal.ZERO) < 0
                || item.getDollarPerKilogram().compareTo(new BigDecimal("7500.00")) > 0) {
            result.addErrorMessage("%/Kg must be between 0.00 and 7500.00.");
        }

        //Edible items must have a value greater than 0.
        if(item.getCategory().equals(Category.EDIBLE) || item.getCategory().equals(Category.MEDICINAL)){
            if (item.getDollarPerKilogram().compareTo(new BigDecimal(0.01)) < 0
                || item.getDollarPerKilogram().compareTo(new BigDecimal("7500.00")) > 0) {
            result.addErrorMessage("$/Kg must be between 0.00 and 7500.00.");
            }
        }

        if (item.getCategory() == null){
            result.addErrorMessage("Item category cannot be null");
        }

        if (!result.isSuccess()) {
            return result;
        }

        result.setPayload(repository.add(item));

        return result;
    }
}
