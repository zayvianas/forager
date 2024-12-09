package learn.foraging.data;

import learn.foraging.models.Forager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForagerFileRepositoryTest {

    static final String SEED_PATH = "./data/items-seed.txt";
    static final String TEST_PATH = "./data/items-test.txt";
    @BeforeEach
    void setup() throws IOException {
        Path seedPath = Paths.get(SEED_PATH);
        Path testPath = Paths.get(TEST_PATH);
        Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void shouldFindAll() {
        ForagerFileRepository repo = new ForagerFileRepository(TEST_PATH);
        List<Forager> all = repo.findAll();
        assertEquals(26, all.size());
    }

    @Test
    void shouldAddForager() throws DataException {
        ForagerFileRepository repo = new ForagerFileRepository(TEST_PATH);
        List<Forager> all = repo.findAll();
        Forager newForager = new Forager("sdf123sdf", "Dave", "Brown", "TX");

        Forager addedForager = repo.addForager(newForager);

        assertEquals(newForager, addedForager);
    }


}