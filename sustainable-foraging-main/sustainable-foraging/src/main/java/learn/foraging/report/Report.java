package learn.foraging.report;

import learn.foraging.domain.ForageService;
import learn.foraging.domain.ForagerService;
import learn.foraging.domain.ItemService;
import learn.foraging.domain.Result;
import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Item;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
public class Report {
    private  ItemService itemService;
    private ForageService forageService;
    private ForagerService foragerService;

    private final String DIR = "./data/forage_data";


    public Report(ForagerService foragerService, ForageService forageService, ItemService itemService) {
        this.foragerService = foragerService;
        this.forageService = forageService;
        this.itemService = itemService;
    }



    public CategoryResult totalValueByCategory (LocalDate date){
        CategoryResult result = new CategoryResult();

        Result exists = validateDate(date);
        if(!exists.isSuccess()){
            result.addErrorMessage(exists.getErrorMessages().get(0));
        }

        List<Forage> allForages = forageService.findByDate(date);

        HashSet<Item> uniqueItems = new HashSet<>();
        for (Forage forage : allForages){
            uniqueItems.add(forage.getItem());
        }


        //Empty Hashmap of totals
        HashMap<Category, BigDecimal> categoryTotals = new HashMap<>();
        for (Category c : Category.values()){
            categoryTotals.put(c, new BigDecimal(0));
        }

        //This adds to totals for each items to the HashMap
        //itemTotals now has all totals
        for (Forage forage : allForages){
            for (Category c : Category.values()){
                if (forage.getItem().getCategory().equals(c)){
                    categoryTotals.replace(c, categoryTotals.get(c).add(forage.getValue()));
                }
            }
        }
//                Stream<Forage> allForagesStream = allForages.stream();
//
//                Map<Category, Long> categoryTotals = allForagesStream
//                        .map(forage -> forage.getItem().getCategory())
//                .collect(Collectors.groupingBy(
//                        Item::getCategory,
//                        Collectors.summarizingDouble(forage -> forage.get))) ;
//
//        for(String result : results.keySet()){
//            System.out.printf("%s %s%n", result, results.get(result));
//        }

        result.setPayload(categoryTotals);

        return result;
    }

    public Result validateDate (LocalDate date){
        Result result = new Result();

        if (alldates().contains(date)){
           return result;
        }
        else {
            result.addErrorMessage("Sorry. There are no forages for this date.");
        }
        return result;
    }

    public KiloResult reportByKilo(LocalDate date) {
        // For each item, add the kilos together under each item
        // Need a list that has all items that has no duplicates , then takes kilos and add it to the count for each item
        HashMap<Item, Double> report = new HashMap<>();
        Double count = 0.0;

        KiloResult result = new KiloResult();
        Result exists = validateDate(date);

        if(!exists.isSuccess()){
            result.addErrorMessage(exists.getErrorMessages().get(0));
            return result;
        }

        List<Forage> forages = forageService.findByDate(date);

        List<Item>allItems = new ArrayList<>();
        for(Forage forage :forages){
            allItems.add(forage.getItem());
        }

        //https://www.geeksforgeeks.org/how-to-remove-duplicates-from-arraylist-in-java/
        List<Item> setItems = allItems.stream()
                .distinct()
                .collect(Collectors.toList());


        if (result.isSuccess()) {
            // Intialize HashMap
            for (Item i : setItems) {
                report.put(i, count);
            }

            for (Item item : setItems) {
                for (Forage forage: forages) {
                    if (item == forage.getItem()) {
                        report.put(item,report.get(item)+ forage.getKilograms());
                    }

                }

            }


        }
        result.setPayload(report);

        return result;
    }



    public HashMap<Category, Integer> reportByCatergory(){
        List<LocalDate> alldates = alldates();
        HashMap<Item, Integer> report = new HashMap<>();


        return null;
    }
    private List<Item> findAllItems(){
        List<Item> allItems = new ArrayList<>();

        for(Category value:Category.values()){
            allItems.addAll(itemService.findByCategory(value));
        }
       return allItems;
    }

    private List<Forage> findAllForages(){
        List<Forage> allForages = new ArrayList<>();
        List<LocalDate> allDates = alldates();


        for(LocalDate date: allDates){
            allForages.addAll(forageService.findByDate(date));
        }
        return allForages;
    }

    private List<LocalDate> alldates(){
        List<String>fileNames = listFiles(DIR);
        List<LocalDate>allDates = new ArrayList<>();

        for(String file:fileNames){
            String stringFile = file.substring(0,file.length()-4).replaceAll("-","/");
            allDates.add(LocalDate.parse(stringFile, DateTimeFormatter.ofPattern("yyyy/MM/dd")));

        }

        return allDates;
    }

// https://www.baeldung.com/java-list-directory-files
    private List<String> listFiles(String dir) {

        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toList());
    }
}
