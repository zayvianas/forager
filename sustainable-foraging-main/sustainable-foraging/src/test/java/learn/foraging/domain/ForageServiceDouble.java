package learn.foraging.domain;

import learn.foraging.data.*;
import learn.foraging.models.Category;
import learn.foraging.models.Forage;
import learn.foraging.models.Forager;
import learn.foraging.models.Item;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ForageServiceDouble {
    private LocalDate date = LocalDate.of(2020,06,26);
    public final static Item EDIBLE = new Item(1, "Ediblw1", Category.EDIBLE, new BigDecimal("1.00"));

    public final static Item EDIBLE2 = new Item(1, "Edible2", Category.EDIBLE, new BigDecimal("5.00"));
    public final static Item POISONOUS = new Item(1, "Poisonous", Category.POISONOUS, new BigDecimal("0.00"));

    private List<Forage> listOfForages= new ArrayList<>();


    public ForageServiceDouble(){

        Forage forage1 = new Forage();
        forage1.setId("498604db-b6d6-4599-qwer-3d8190fda823");
        forage1.setDate(date);
        forage1.setForager(makeForager());
        forage1.setItem(ForageServiceDouble.EDIBLE);
        forage1.setKilograms(1.25);
        listOfForages.add(forage1);

        Forage forage2 = new Forage();
        forage2.setId("498604db-b6d6-4599-asdf-3d8190fda823");
        forage2.setDate(date);
        forage2.setForager(makeForager());
        forage2.setItem(ForageServiceDouble.EDIBLE2);
        forage2.setKilograms(1.25);
        listOfForages.add(forage2);

        Forage forage3 = new Forage();
        forage3.setId("498604db-b6d6-4599-zxcv-3d8190fda823");
        forage3.setDate(date);
        forage3.setForager(makeForager());
        forage3.setItem(ForageServiceDouble.POISONOUS);
        forage3.setKilograms(1.25);
        listOfForages.add(forage3);

        Forage forage4 = new Forage();
        forage4.setId("498604db-b6d6-4599-ghjk-3d8190fda823");
        forage4.setDate(LocalDate.of(1900, 01, 01));
        forage4.setForager(makeForager());
        forage4.setItem(ForageServiceDouble.POISONOUS);
        forage4.setKilograms(1.25);
        listOfForages.add(forage4);


    }


    public List<Forage> findByDate(LocalDate date) {
        List<Forage> resultList = new ArrayList<>();
        for(Forage forage : listOfForages){
            if (forage.getDate().compareTo(date) == 0){
                resultList.add(forage);
            }
        }
       return resultList;
    }

    private static Forager makeForager() {
        Forager forager = new Forager();
        forager.setId("0e4707f4-407e-4ec9-9665-baca0aabe88c");
        forager.setFirstName("Jilly");
        forager.setLastName("Sisse");
        forager.setState("GA");
        return forager;
    }
}
