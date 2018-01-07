package cz.muni.fi.pa165.mushrooms.sample;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.muni.fi.pa165.mushrooms.entity.Forest;
import cz.muni.fi.pa165.mushrooms.entity.Mushroom;
import cz.muni.fi.pa165.mushrooms.entity.MushroomHunter;
import cz.muni.fi.pa165.mushrooms.entity.Visit;
import cz.muni.fi.pa165.mushrooms.enums.MushroomType;
import cz.muni.fi.pa165.mushrooms.service.ForestService;
import cz.muni.fi.pa165.mushrooms.service.MushroomHunterService;
import cz.muni.fi.pa165.mushrooms.service.MushroomService;
import cz.muni.fi.pa165.mushrooms.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Component
@Transactional //transactions are handled on facade layer
public class SampleDataLoadingFacadeImpl implements SampleDataLoadingFacade {

    final static Logger log = LoggerFactory.getLogger(SampleDataLoadingFacadeImpl.class);
    private static final String DEFAULT_PASSWORD = "Password.123";

    @Inject
    private MushroomHunterService hunterService;

    @Inject
    private MushroomService mushroomService;

    @Inject
    private ForestService forestService;

    @Inject
    private VisitService visitService;

    private Map<String, MushroomHunter> hunters = new HashMap<>();
    private Map<String, Mushroom> mushrooms = new HashMap<>();
    private Map<String, Forest> forests = new HashMap<>();

    @Override
    public void loadData() {
        loadMushroomHunters();
        loadMushrooms();
        loadForests();
        loadVisits();
    }

    private void loadVisits() {
        log.info("Creating visits.");
        createVisit("First time visit", LocalDate.ofEpochDay(50), hunters.get("john"), forests.get("magic forest"));
        createVisit("Last time visit", LocalDate.now(), hunters.get("dennis"), forests.get("deep forest"));
        createVisit("Some other visit", LocalDate.ofEpochDay(2200), hunters.get("benny"), forests.get("deep forest"));
        createVisit("Some new visit", LocalDate.ofEpochDay(2300), hunters.get("benny"), forests.get("deep forest"), new ArrayList<Mushroom>(mushrooms.values()));
        log.info("Visits have been created!");
    }

    private void loadForests() {
        log.info("Creating forests.");
        createForest("Magic forest", "Super magic ultra woo!");
        createForest("Deep forest", "Soo deep!");
        createForest("Scary forest", "Soo ultimate bo bo bo scary!");
        createForest("Normal forest", "Nothing interesting about it.");
        log.info("Forests have been created!");
    }

    private void loadMushrooms() {
        log.info("Creating mushrooms");
        createMushroom("Dubak", MushroomType.EDIBLE, "march", "june");
        createMushroom("Plavka", MushroomType.INEDIBLE, "april", "september");
        createMushroom("Muchotravka", MushroomType.POISONOUS, "july", "december");
        createMushroom("MagicShroom", MushroomType.EDIBLE, "january", "december");
        createMushroom("PoisonShroom", MushroomType.POISONOUS, "december", "january");
        createMushroom("DoNotEatMe", MushroomType.INEDIBLE, "october", "april");
        log.info("Mushrooms has been created!");
    }

    private void loadMushroomHunters() {
        log.info("Creating Mushroom hunters");
        createHunter("John", "Snow");
        createHunter("Benny", "Newman");
        createHunter("Anna", "Karenina","Russia4Ever!", false, "This is me!");
        createHunter("Dennis", "Ritchie", true);
        createHunter("Admin", "Project", true);
        createHunter("George", "Miller", true);
        log.info("Mushroom hunters has been created!");
    }

    private void createHunter(String name, String last) {
        createHunter(name, last, DEFAULT_PASSWORD, false, null);
    }

    private void createHunter(String name, String last, boolean admin) {
        createHunter(name, last, DEFAULT_PASSWORD, admin, null);
    }

    private void createHunter(String name, String last, String password, boolean admin, String personalInfo) {
        MushroomHunter hunter = new MushroomHunter();
        hunter.setPersonalInfo(personalInfo);
        hunter.setFirstName(name);
        hunter.setSurname(last);
        hunter.setAdmin(admin);
        hunter.setUserNickname(name.toLowerCase());
        hunterService.registerHunter(hunter, password);
        log.debug("Creating hunter: \"" + name.toLowerCase() + "\": " + hunter);
        hunters.put(name.toLowerCase(), hunter);
    }

    private void createForest(String name, String desc) {
        Forest forest = new Forest();
        forest.setName(name);
        forest.setDescription(desc);
        forestService.createForest(forest);
        log.debug("Creating forest: \"" + name.toLowerCase() + "\": " + forest);
        forests.put(name.toLowerCase(), forest);
    }

    private void createVisit(String note, LocalDate date, MushroomHunter hunter, Forest forest) {
        Visit visit = new Visit();
        visit.setNote(note);
        visit.setDate(date);
        visit.setHunter(hunter);
        visit.setForest(forest);
        visitService.createVisit(visit);
        log.debug("Creating visit: " + visit);
    }

    private void createVisit(String note, LocalDate date, MushroomHunter hunter, Forest forest, List<Mushroom> mushrooms) {
        Visit visit = new Visit();
        visit.setNote(note);
        visit.setDate(date);
        visit.setMushrooms(mushrooms);
        visit.setHunter(hunter);
        visit.setForest(forest);
        visitService.createVisit(visit);
        log.debug("Creating visit: " + visit);
    }

    private void createMushroom(String name, MushroomType type, String from, String to) {
        Mushroom mushroom = new Mushroom();
        mushroom.setName(name);
        mushroom.setType(type);
        mushroom.setIntervalOfOccurrence(from, to);
        mushroomService.createMushroom(mushroom);
        log.debug("Creating mushroom: \"" + name.toLowerCase() + "\": " + mushroom);
        mushrooms.put(name.toLowerCase(), mushroom);
    }
}
