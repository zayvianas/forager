package learn.foraging;

import learn.foraging.ui.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan
@PropertySource("classpath:data.properties")
public class App {
    public static void main(String[] args) {
        //test
//        ConsoleIO io = new ConsoleIO();
////        View view = new View(io);
////
////        ForageFileRepository forageFileRepository = new ForageFileRepository("./data/forage_data");
////        ForagerFileRepository foragerFileRepository = new ForagerFileRepository("./data/foragers.csv");
////        ItemFileRepository itemFileRepository = new ItemFileRepository("./data/items.txt");
////
////        ForagerService foragerService = new ForagerService(foragerFileRepository);
////        ForageService forageService = new ForageService(forageFileRepository, foragerFileRepository, itemFileRepository);
////        ItemService itemService = new ItemService(itemFileRepository);
////        Report report =new Report(foragerService,forageService,itemService);
////        Controller controller = new Controller(foragerService, forageService, itemService, view,report);

        ApplicationContext ctx = new AnnotationConfigApplicationContext(App.class);
        Controller controller = ctx.getBean(Controller.class);
        controller.run();
    }
}
