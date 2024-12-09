package learn.foraging.ui;

import learn.foraging.models.*;

import learn.foraging.report.CategoryResult;

import learn.foraging.report.Report;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class View {

    private final ConsoleIO io;

    public View(ConsoleIO io) {
        this.io = io;
    }

    public MainMenuOption selectMainMenuOption() {
        displayHeader("Main Menu");
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (MainMenuOption option : MainMenuOption.values()) {
            if (!option.isHidden()) {
                io.printf("%s. %s%n", option.getValue(), option.getMessage());
            }
            min = Math.min(min, option.getValue());
            max = Math.max(max, option.getValue());
        }

        String message = String.format("Select [%s-%s]: ", min, max - 1);
        return MainMenuOption.fromValue(io.readInt(message, min, max));
    }

    public LocalDate getForageDate() {
        displayHeader(MainMenuOption.VIEW_FORAGES_BY_DATE.getMessage());
        return io.readLocalDate("Select a date [mm/dd/yyyy]: ");
    }

    public String getForagerState() {
        displayHeader(MainMenuOption.VIEW_FORAGERS_BY_STATE.getMessage());
        return io.readRequiredState("Please enter a state abbreviation [MN]: ");
    }

    public String getForagerNamePrefix() {
        return io.readRequiredString("Forager last name starts with: ");
    }

    public Forager chooseForager(List<Forager> foragers) {
        if (foragers.size() == 0) {
            io.println("No foragers found");
            return null;
        }

        int index = 1;
        for (Forager forager : foragers.stream().limit(25).collect(Collectors.toList())) {
            io.printf("%s: %s %s%n", index++, forager.getFirstName(), forager.getLastName());
        }
        index--;

        if (foragers.size() > 25) {
            io.println("More than 25 foragers found. Showing first 25. Please refine your search.");
        }
        io.println("0: Exit");
        String message = String.format("Select a forager by their index [0-%s]: ", index);

        index = io.readInt(message, 0, index);
        if (index <= 0) {
            return null;
        }
        return foragers.get(index - 1);
    }

    public Category getItemCategory() {
        displayHeader("Item Categories");
        int index = 1;
        for (Category c : Category.values()) {
            io.printf("%s: %s%n", index++, c);
        }
        index--;
        String message = String.format("Select a Category [1-%s]: ", index);
        return Category.values()[io.readInt(message, 1, index) - 1];
    }

    public Item chooseItem(List<Item> items) {

        displayItems(items);

        if (items.size() == 0) {
            return null;
        }

        int itemId = io.readInt("Select an item id: ");
        Item item = items.stream()
                .filter(i -> i.getId() == itemId)
                .findFirst()
                .orElse(null);

        if (item == null) {
            displayStatus(false, String.format("No item with id %s found.", itemId));
        }

        return item;
    }

    public Forage makeForage(Forager forager, Item item) {
        Forage forage = new Forage();
        forage.setForager(forager);
        forage.setItem(item);
        forage.setDate(io.readLocalDate("Forage date [MM/dd/yyyy]: "));
        String message = String.format("Kilograms of %s: ", item.getName());
        forage.setKilograms(io.readDouble(message, 0.001, 250.0));
        return forage;
    }

    public Forager addForager(){
        Forager newForager = new Forager();
        String firstName = io.readRequiredString("Please enter a first name: ");
        String lastName = io.readRequiredString("Please enter a last name: ");
        String state = io.readRequiredState("Please enter a state [e.g. TX]: ");
        String id = java.util.UUID.randomUUID().toString();

        newForager.setFirstName(firstName);
        newForager.setLastName(lastName);
        newForager.setState(state);
        newForager.setId(id);

        return newForager;
    }

    public Item makeItem() {
        displayHeader(MainMenuOption.ADD_ITEM.getMessage());
        Item item = new Item();
        item.setCategory(getItemCategory());
        item.setName(io.readRequiredString("Item Name: "));
        item.setDollarPerKilogram(io.readBigDecimal("$/Kg: ", BigDecimal.ZERO, new BigDecimal("7500.00")));
        return item;
    }

    public Forager getForagerUpdates(Forager oldForager){
        //Had to create new solar panel because update was changing the reference
        Forager updatedForager = new Forager();
        updatedForager.setId(oldForager.getId());

        //Row
        String firstName = io.readStringWithBlank(String.format("First Name (%s): ", oldForager.getFirstName()), oldForager);
        //Col
        String lastName = io.readStringWithBlank(String.format("Last Name (%s): ", oldForager.getLastName()), oldForager);
        //Section
        String state = io.readStateWithBlank(String.format("State (%s): ", oldForager.getState()), oldForager);

        updatedForager.setFirstName(firstName);
        updatedForager.setLastName(lastName);
        updatedForager.setState(state);
        return updatedForager;
    }

    public void displayUpdatedForager(Forager oldForager, Forager newForager){
        displayHeader("Old Forager:");
        displayForager(oldForager);
        displayHeader("New Forager:");
        displayForager(newForager);
    }
    public void displayReportByKilos(HashMap<Item,Double> report){
        for(Item item:report.keySet()){
            System.out.printf("%15s : %.2f Kg\n",item.getName(),report.get(item));
        }

    }

    public GenerateRequest getGenerateRequest() {
        displayHeader(MainMenuOption.GENERATE.getMessage());
        LocalDate start = io.readLocalDate("Select a start date [mm/dd/yyyy]: ");
        if (start.isAfter(LocalDate.now())) {
            displayStatus(false, "Start date must be in the past.");
            return null;
        }

        LocalDate end = io.readLocalDate("Select an end date [mm/dd/yyyy]: ");
        if (end.isAfter(LocalDate.now()) || end.isBefore(start)) {
            displayStatus(false, "End date must be in the past and after the start date.");
            return null;
        }

        GenerateRequest request = new GenerateRequest();
        request.setStart(start);
        request.setEnd(end);
        request.setCount(io.readInt("Generate how many random forages [1-500]?: ", 1, 500));
        return request;
    }

    public void enterToContinue() {
        io.readString("Press [Enter] to continue.");
    }

    public void displayCategoryReport (HashMap<Category, BigDecimal> hashMap){
        displayHeader("Total value of each Category collected");
        for (Category c : hashMap.keySet()){
            System.out.printf("%s: ", c.toString());
            if (hashMap.get(c).compareTo(new BigDecimal(0)) == 0){
                System.out.println("$0.00");
            } else {
                System.out.printf("$%s%n", hashMap.get(c).setScale(2, RoundingMode.HALF_UP));
            }
        }
    }
    // display only
    public void displayHeader(String message) {
        io.println("");
        io.println(message);
        io.println("=".repeat(message.length()));
    }

    public void displayException(Exception ex) {
        displayHeader("A critical error occurred:");
        io.println(ex.getMessage());
    }

    public void displayStatus(boolean success, String message) {
        displayStatus(success, List.of(message));
    }

    public void displayStatus(boolean success, List<String> messages) {
        displayHeader(success ? "Success" : "Error");
        for (String message : messages) {
            io.println(message);
        }
    }

    public void displayForages(List<Forage> forages) {
        if (forages == null || forages.isEmpty()) {
            io.println("No forages found.");
            return;
        }
        for (Forage forage : forages) {
            io.printf("%s %s - %s:%s - Value: $%.2f%n",
                    forage.getForager().getFirstName(),
                    forage.getForager().getLastName(),
                    forage.getItem().getName(),
                    forage.getItem().getCategory(),
                    forage.getValue()
            );
        }
    }

    public void displayForagers(List<Forager> foragers) {
        if (foragers == null || foragers.isEmpty()) {
            io.println("No foragers found.");
            return;
        }
        io.println("     First Name     Last Name                  ID       ");
        for (Forager forager : foragers) {

            io.printf("%15s %15s %20s%n",
                    forager.getFirstName(),
                    forager.getLastName(),
                    forager.getId()
            );
        }
    }

    public void displayItems(List<Item> items) {

        if (items.size() == 0) {
            io.println("No items found");
        }

        for (Item item : items) {
            io.printf("%s: %s, %s, %.2f $/kg%n", item.getId(), item.getName(), item.getCategory(), item.getDollarPerKilogram());
        }
    }

    private void displayForager(Forager forager) {
        io.println("     First Name       Last Name State");
        io.printf("%15s %15s %5s%n",
                forager.getFirstName(),
                forager.getLastName(),
                forager.getState()
        );
    }

}
