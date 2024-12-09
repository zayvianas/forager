package learn.foraging.data;

import learn.foraging.models.Forager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Repository
public class ForagerFileRepository implements ForagerRepository {

    private final String filePath;

    private final String HEADER = "id,first_name,last_name,state";

    public ForagerFileRepository(@Value("${foragerFilePath}") String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Forager> findAll() {
        ArrayList<Forager> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            reader.readLine(); // read header

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                String[] fields = line.split(",", -1);
                if (fields.length == 4) {
                    result.add(deserialize(fields));
                }
            }
        } catch (IOException ex) {
            // don't throw on read
        }
        return result;
    }

    @Override
    public Forager findById(String id) {
        return findAll().stream()
                .filter(i -> i.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Forager> findByState(String stateAbbr) {
        return findAll().stream()
                .filter(i -> i.getState().equalsIgnoreCase(stateAbbr))
                .collect(Collectors.toList());
    }

    @Override
    public Forager addForager(Forager forager) throws DataException {
        List<Forager> all = findAll();
        all.add(forager);
        writeAll(all);
        return forager;
    }
    @Override
    public boolean update(Forager forager) throws DataException {
        List<Forager> all = findAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(forager.getId())) {
                all.set(i, forager);
                writeAll(all);
                return true;
            }
        }
        return false;
    }

    private void writeAll(List<Forager> foragers) throws DataException {
        try (PrintWriter writer = new PrintWriter(filePath)) {

            writer.println(HEADER);

            for (Forager forager : foragers) {
                writer.println(serialize(forager));
            }
        } catch (FileNotFoundException ex) {
            throw new DataException(ex);
        }
    }

    private String serialize(Forager forager) {
        return String.format("%s,%s,%s,%s",
                forager.getId(),
                forager.getFirstName(),
                forager.getLastName(),
                forager.getState());
    }
    
    private Forager deserialize(String[] fields) {
        Forager result = new Forager(
        fields[0],
        fields[1],
        fields[2],
        fields[3]);
        return result;
    }


}
