package learn.foraging.ui;

import learn.foraging.data.DataException;
import learn.foraging.domain.ForageService;
import learn.foraging.domain.ForagerService;
import learn.foraging.domain.ItemService;
import learn.foraging.domain.Result;
import learn.foraging.models.*;
import learn.foraging.report.CategoryResult;
import learn.foraging.report.KiloResult;
import learn.foraging.report.Report;

import java.time.LocalDate;
import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {

    private final ForagerService foragerService;
    private final ForageService forageService;
    private final ItemService itemService;
    private final View view;
    private final Report report;

    public Controller(ForagerService foragerService, ForageService forageService, ItemService itemService, View view, Report report) {
        this.foragerService = foragerService;
        this.forageService = forageService;
        this.itemService = itemService;
        this.view = view;
        this.report = report;
    }


    public void run() {
        view.displayHeader("Welcome to Sustainable Foraging");
        try {
            runAppLoop();
        } catch (DataException ex) {
            view.displayException(ex);
        }
        view.displayHeader("Goodbye.");
    }

    private void runAppLoop() throws DataException {
        MainMenuOption option;
        do {
            option = view.selectMainMenuOption();
            switch (option) {
                case VIEW_FORAGES_BY_DATE:
                    viewByDate();
                    break;
                case VIEW_FORAGERS_BY_STATE:
                    viewByState();
                    break;
                case VIEW_ITEMS:
                    viewItems();
                    break;
                case ADD_FORAGE:
                    addForage();
                    break;
                case ADD_FORAGER:
                    addForager();
                    break;
                case ADD_ITEM:
                    addItem();
                    break;
                case UPDATE_FORAGER:
                    updateForager();
                    break;
                case REPORT_KG_PER_ITEM:
                    reportByKilo();
                    break;
                case REPORT_CATEGORY_VALUE:
                    displayCategoryValueReport();
                    break;
                case GENERATE:
                    generate();
                    break;
            }
        } while (option != MainMenuOption.EXIT);
    }

    // top level menu
    private void viewByDate() {
        LocalDate date = view.getForageDate();
        List<Forage> forages = forageService.findByDate(date);
        view.displayForages(forages);
        view.enterToContinue();
    }

    private void viewByState(){
        String state = view.getForagerState();
        List<Forager> forages = foragerService.findByState(state);
        view.displayForagers(forages);
        view.enterToContinue();
    }

    private void viewItems() {
        view.displayHeader(MainMenuOption.VIEW_ITEMS.getMessage());
        Category category = view.getItemCategory();
        List<Item> items = itemService.findByCategory(category);
        view.displayHeader("Items");
        view.displayItems(items);
        view.enterToContinue();
    }

    private void addForage() throws DataException {
        view.displayHeader(MainMenuOption.ADD_FORAGE.getMessage());
        Forager forager = getForager();
        if (forager == null) {
            return;
        }
        Item item = getItem();
        if (item == null) {
            return;
        }
        Forage forage = view.makeForage(forager, item);
        Result<Forage> result = forageService.add(forage);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Forage %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
        view.enterToContinue();
    }

    private void addForager() throws DataException {
        view.displayHeader(MainMenuOption.ADD_FORAGER.getMessage());
        Forager newForager = view.addForager();
        Result<Forager> result = foragerService.addForager(newForager);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Forager %s %s created.", result.getPayload().getFirstName(), result.getPayload().getLastName());
            view.displayStatus(true, successMessage);
        }
        view.enterToContinue();
    }

    private void addItem() throws DataException {
        Item item = view.makeItem();
        Result<Item> result = itemService.add(item);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            String successMessage = String.format("Item %s created.", result.getPayload().getId());
            view.displayStatus(true, successMessage);
        }
        view.enterToContinue();
    }

    private void displayCategoryValueReport(){
        LocalDate date = view.getForageDate();
        CategoryResult result = report.totalValueByCategory(date);
        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            view.displayCategoryReport(result.getPayload());
        }
        view.enterToContinue();
    }

    private void updateForager() throws DataException {
        view.displayHeader(MainMenuOption.UPDATE_FORAGER.getMessage());
        Forager oldForager = getForager();

        Forager updatedForager = view.getForagerUpdates(oldForager);
        Result<Forager> result = foragerService.updateForager(updatedForager);

        if (!result.isSuccess()) {
            view.displayStatus(false, result.getErrorMessages());
        } else {
            view.displayUpdatedForager(oldForager, updatedForager);
        }
        view.enterToContinue();
    }

    private void generate() throws DataException {
        GenerateRequest request = view.getGenerateRequest();
        if (request != null) {
            int count = forageService.generate(request.getStart(), request.getEnd(), request.getCount());
            view.displayStatus(true, String.format("%s forages generated.", count));
        }
    }

    // support methods
    private Forager getForager() {
        String lastNamePrefix = view.getForagerNamePrefix();
        List<Forager> foragers = foragerService.findByLastName(lastNamePrefix);
        return view.chooseForager(foragers);
    }

    private Item getItem() {
        Category category = view.getItemCategory();
        List<Item> items = itemService.findByCategory(category);
        return view.chooseItem(items);
    }

    private void reportByKilo(){
        view.displayHeader("Report By Kilo");

        LocalDate date = view.getForageDate();
        KiloResult reportByKilo =report.reportByKilo(date);
        view.displayReportByKilos(reportByKilo.getPayload());
        view.enterToContinue();
    }
}
