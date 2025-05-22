package edu.pja.sii_lat;
import edu.pja.sii_lat.DTO.AssignCollectionBoxReq;
import edu.pja.sii_lat.DTO.CreateEventReq;
import edu.pja.sii_lat.DTO.DepositMoneyReq;
import edu.pja.sii_lat.model.CollectionBox;
import edu.pja.sii_lat.model.Event;
import edu.pja.sii_lat.repository.CollectionBoxRepository;
import edu.pja.sii_lat.repository.EventRepository;
import edu.pja.sii_lat.service.ICollectionBoxService;
import edu.pja.sii_lat.service.IEventService;
import edu.pja.sii_lat.service.IExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final IExchangeRateService exchangeRateService;
    private final IEventService eventService;
    private final ICollectionBoxService collectionBoxService;

    private final CollectionBoxRepository collectionBoxRepository;
    private final EventRepository eventRepository;


    @Override
    public void run(ApplicationArguments args){
        //manualServiceTests();

        Event event = new Event();
        event.setName("Event 1");
        event.setFundsCurrencyCode("USD");
        event.setFunds(2000);

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setEvent(event);
        event.getCollectionBoxSet().add(collectionBox);
        collectionBox.getFunds().put("PLN", 100.0);
        collectionBox.getFunds().put("USD", 14.0);

        eventRepository.save(event);
        collectionBoxRepository.save(collectionBox);

        Event event1 = new Event();
        event1.setName("Event 2");
        event1.setFundsCurrencyCode("PLN");

        CollectionBox collectionBox1 = new CollectionBox();
        collectionBox1.setEvent(event1);
        event1.getCollectionBoxSet().add(collectionBox1);
        collectionBox1.getFunds().put("PLN", 2.88);
        collectionBox1.getFunds().put("USD", 999.44);

        eventRepository.save(event1);
        collectionBoxRepository.save(collectionBox1);

    }

    /**
     * testing services manually because mocks suddenly stopped working
     */
    private void manualServiceTests(){
        // getRateFor
        System.out.println("================= getRateFor =================");
        System.out.println(exchangeRateService.getRateFor("USD", "PLN"));
        // getValidCurrencies
        System.out.println("================= getValidCurrencies =================");
        System.out.println(exchangeRateService.getValidCurrencies());

        // create event
        System.out.println("================= create event =================");
        CreateEventReq request = new CreateEventReq("TEST", "PLN");
        System.out.println(eventService.createEvent(request));

        System.out.println("================= generateFinancialReport =================");
        System.out.println(eventService.generateFinancialReport());

        System.out.println("================= registerCollectionBox =================");
        System.out.println(collectionBoxService.registerCollectionBox());

        System.out.println("================= listCollectionBoxes =================");
        System.out.println(collectionBoxService.listCollectionBoxes());

        System.out.println("================= assignCollectionBox =================");
        System.out.println(collectionBoxService.assignCollectionBox(new AssignCollectionBoxReq(1, 1)));

        System.out.println("================= depositMoney =================");
        System.out.println(collectionBoxService.depositMoney(new DepositMoneyReq(1,100, "USD")));

        System.out.println("================= emptyCollectionBox =================");
        System.out.println(collectionBoxService.emptyCollectionBox(1));

        System.out.println("================= unregisterCollectionBox =================");
        collectionBoxService.unregisterCollectionBox(1);
        System.out.println(collectionBoxService.listCollectionBoxes());
    }
}
