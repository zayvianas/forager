package learn.foraging.domain;

import learn.foraging.data.DataException;
import learn.foraging.data.ForagerRepository;
import learn.foraging.models.Forager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ForagerService {

    private final ForagerRepository repository;

    public ForagerService(ForagerRepository repository) {
        this.repository = repository;
    }

    public List<Forager> findByState(String stateAbbr) {
        return repository.findByState(stateAbbr);
    }

    public List<Forager> findByLastName(String prefix) {
        return repository.findAll().stream()
                .filter(i -> i.getLastName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Result<Forager> addForager(Forager forager) throws DataException {
        Result<Forager> result = new Result<>();
        result = validate(forager);

        if (!result.isSuccess()){
            result.addErrorMessage("Unable to add forager.");
            return result;
        }

        result.setPayload(repository.addForager(forager));

        return result;
    }

    public Result<Forager> updateForager(Forager forager) throws DataException{
        Result<Forager> result = new Result<>();
        boolean successfulUpdate = repository.update(forager);
        if (!successfulUpdate){
            result.addErrorMessage("Could not update this forager");
            return result;
        }
        result.setPayload(forager);
        return result;
    }

    private Result<Forager> validate(Forager forager) {

        Result<Forager> result = validateNulls(forager);
        if (!result.isSuccess()) {
            return result;
        }

        result = validateFields(forager, result);
        if (!result.isSuccess()) {
            return result;
        }
        return result;
    }

    private Result<Forager> validateNulls(Forager forager) {
        //creating of new Result<Forager> starts here.
        Result<Forager> result = new Result<>();

        if (forager == null) {
            result.addErrorMessage("Forager cannot be null.");
            return result;
        }

        if (forager.getId() == null) {
            result.addErrorMessage("Forager Id cannot be null.");
        }

        if (forager.getFirstName() == null) {
            result.addErrorMessage("Forager First name cannot be null.");
        }

        if (forager.getLastName() == null) {
            result.addErrorMessage("Last name cannot be null.");
        }

        if (forager.getState() == null) {
            result.addErrorMessage("State cannot be null.");
        }
        return result;
    }

    private Result<Forager> validateFields(Forager forager, Result<Forager> result) {

        if (forager.getId().isEmpty() || forager.getId().isBlank()) {
            result.addErrorMessage("Forager id is required");
        }
        if (forager.getFirstName().isEmpty() || forager.getFirstName().isBlank()) {
            result.addErrorMessage("Forager first name is required");
        }
        if (forager.getLastName().isEmpty() || forager.getLastName().isBlank()) {
            result.addErrorMessage("Forager last name is required");
        }
        if (forager.getState().isEmpty() || forager.getState().isBlank()) {
            result.addErrorMessage("State is required");
        }

        if (alreadyExistsInDatabase(forager)){
            result.addErrorMessage("A forager with this first name, last name, and state already exists in the database.");
        }

        return result;

    }

    private boolean alreadyExistsInDatabase(Forager forager){
        List<Forager> allForagers = repository.findAll();
        for (Forager f: allForagers){
            if (forager.isSameAs(f)){
                return true;
            }
        }
        return false;
    }
}
