package learn.foraging.report;

import learn.foraging.data.*;
import learn.foraging.domain.ForageService;
import learn.foraging.domain.ForagerService;
import learn.foraging.domain.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;



class ReportTest {

    ForagerService foragerService ;

    private Report report;

    @BeforeEach
    void setup() {
        ItemRepository itemRepository = new ItemRepositoryDouble();
        ForageRepository forageRepository = new ForageRepositoryDouble();
        ForagerRepository foragerRepository = new ForagerRepositoryDouble();

        ItemService itemService = new ItemService(itemRepository);
        ForagerService foragerService = new ForagerService(foragerRepository);
    ForageService forageService = new ForageService(forageRepository, foragerRepository, itemRepository);

        report = new Report(foragerService, forageService, itemService);
    }


    @Test
    void shouldFindReportsForDate() {
        KiloResult reportResult = report.reportByKilo(LocalDate.of(2020,06,26));

        assertTrue(reportResult.isSuccess());
    }

    @Test
    void shouldNotFindReportsForADateThatIsNotInRepo() {
        KiloResult reportResult = report.reportByKilo(LocalDate.of(1900,01,01));

        assertFalse(reportResult.isSuccess());
        assertEquals("Sorry. There are no forages for this date.", reportResult.getErrorMessages().get(0));
    }

    @Test
    void shouldFindReportsForCategoriesForDate(){
        CategoryResult result = report.totalValueByCategory(LocalDate.of(2020,06,26));

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotFindReportsForCategoriesForDate(){
        CategoryResult result = report.totalValueByCategory(LocalDate.of(1900,01,01));

        assertFalse(result.isSuccess());
        assertEquals("Sorry. There are no forages for this date.", result.getErrorMessages().get(0));
    }
}