package cz.muni.fi.pa165.mushrooms.service;

import cz.muni.fi.pa165.mushrooms.dao.VisitDao;
import cz.muni.fi.pa165.mushrooms.entity.Forest;
import cz.muni.fi.pa165.mushrooms.entity.Mushroom;
import cz.muni.fi.pa165.mushrooms.entity.MushroomHunter;
import cz.muni.fi.pa165.mushrooms.entity.Visit;
import cz.muni.fi.pa165.mushrooms.enums.MushroomType;
import cz.muni.fi.pa165.mushrooms.service.config.ServiceConfiguration;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cz.muni.fi.pa165.mushrooms.service.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Extensive tests for service layer related to Visit entity.
 *
 * @author bkompis
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class VisitServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Injectable
    private VisitDao visitDao;

    @Tested(fullyInitialized = true)
    private VisitServiceImpl service;

    private MushroomHunter hunter1;
    private MushroomHunter hunter2;
    private Forest forest1;
    private Forest forest2;
    private Visit visit1;
    private Visit visit2;
    private Mushroom mushroom1;
    private Mushroom mushroom2;

    private LocalDate time1 = LocalDate.of(2017, Month.JUNE, 15);
    private LocalDate time2 = LocalDate.of(2017, Month.JULY, 10);

    private Map<Long, Visit> persistedVisits;
    // stores 'database' size + 1 (for indexing like in a real db)
    private long dbCounter = 1;

    @Before
    public void setUp() {
        hunter1 = createHunter("Alphonse", "Elric", "theGoodGuy", false);
        hunter1.setId(1L);
        hunter2 = createHunter("Edward", "Elric", "fullmetal", true);
        hunter2.setId(2L);
        forest1 = createForest("Nice", "a nice forest");
        forest1.setId(1L);
        forest2 = createForest("Normal", "a normal forest");
        forest2.setId(2L);

        mushroom1 = createMushroom("Hrib Dubovy", MushroomType.EDIBLE, "June", "July");
        mushroom1.setId(1L);
        mushroom2 = createMushroom("Hrib Satan", MushroomType.POISONOUS, "July", "September");
        mushroom1.setId(2L);

        visit1 = createVisit(hunter1, forest2, time1);
        visit1.addMushroom(mushroom1);
        visit2 = createVisit(hunter2, forest1, time2);
        visit2.addMushroom(mushroom1);
        visit2.addMushroom(mushroom2);

        new Expectations() {{
            visitDao.create((Visit) any);
            result = new Delegate() {
                void foo(Visit visit) {
                    if (visit == null) {
                        throw new IllegalArgumentException("null");
                    }
                    if (visit.getId() != null) {
                        throw new IllegalArgumentException("already in db");
                    }
                    if (!checkVisitValidity(visit)) {
                        throw new IllegalArgumentException("invalid entity");
                    }
                    if (checkVisitDuplicity(persistedVisits, visit)) {
                        throw new IllegalArgumentException("duplicate");
                    }

                    visit.setId(dbCounter);
                    persistedVisits.put(dbCounter, visit);
                    dbCounter++;
                }
            };
            minTimes = 0;

            visitDao.delete((Visit) any);
            result = new Delegate() {
                void foo(Visit visit) {
                    if (visit == null || visit.getId() == null) {
                        throw new IllegalArgumentException("invalid entity");
                    }
                    if (persistedVisits.get(visit.getId()) == null) {
                        throw new IllegalArgumentException("not in db");
                    }
                    persistedVisits.remove(visit.getId());
                }
            };
            minTimes = 0;

            visitDao.update((Visit) any);
            result = new Delegate() {
                void foo(Visit visit) {
                    if (visit == null) {
                        throw new IllegalArgumentException("null entity");
                    }
                    if (visit.getId() == null) {
                        throw new IllegalArgumentException("null entity id");
                    }
                    if (checkVisitDuplicity(persistedVisits, visit)) {
                        throw new IllegalArgumentException("duplicate after update");
                    }
                    if (!checkVisitValidity(visit)) {
                        throw new IllegalArgumentException("invalid entity");
                    }
                    persistedVisits.replace(visit.getId(), visit);
                }
            };
            minTimes = 0;

            visitDao.findById((Long) any);
            result = new Delegate() {
                Visit foo(Long id) {
                    if (id == null) {
                        throw new IllegalArgumentException("null id");
                    }
                    return persistedVisits.get(id);
                }
            };
            minTimes = 0;

            visitDao.findAll();
            result = new Delegate() {
                List<Visit> foo() {
                    return Collections.unmodifiableList(new ArrayList<>(persistedVisits.values()));
                }
            };
            minTimes = 0;

            visitDao.findByDate((LocalDate) any, (LocalDate) any);
            result = new Delegate() {
                List<Visit> foo(LocalDate from, LocalDate to) {
                    if (from == null || to == null) {

                        throw new IllegalArgumentException("null date");
                    }
                    List<Visit> res = new ArrayList<>();
                    for (Visit v : persistedVisits.values()) {
                        if (v.getDate().isAfter(from) && v.getDate().isBefore(to)) {
                            res.add(v);
                        }
                    }
                    return res;
                }
            };
            minTimes = 0;

        }};

        // "persist" visits:
        // does not use mocked service, to be sure
        persistedVisits = new HashMap<>();
        persistedVisits.put(dbCounter, visit1);
        visit1.setId(dbCounter);
        dbCounter++;
        persistedVisits.put(dbCounter, visit2);
        visit2.setId(dbCounter);
        dbCounter++;
    }

    @Test
    public void findVisitById_valid() {
        assertThat(service.findVisitById(1L)).isEqualTo(visit1);
        assertThat(service.findVisitById(2L)).isEqualTo(visit2);
    }

    @Test
    public void findVisitById_unknownId() {
        assertThat(service.findVisitById(200L)).isEqualTo(null);
    }

    @Test
    public void findVisitById_nullId() {
        assertThatThrownBy(() -> service.findVisitById(null)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void findAllVisits() {
        assertThat(service.findAllVisits()).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    public void findVisitByDate_valid_both() {
        assertThat(service.findVisitByDate(time1.minusDays(1), time2.plusDays(1))).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    public void findVisitByDate_valid_earlier() {
        assertThat(service.findVisitByDate(time1.minusDays(1), time1.plusDays(2))).containsExactlyInAnyOrder(visit1);
    }

    @Test
    public void findVisitByDate_valid_later() {
        assertThat(service.findVisitByDate(time2.minusDays(2), time2.plusDays(1))).containsExactlyInAnyOrder(visit2);
    }

    @Test
    public void createVisit_valid() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        Visit newVisit = createVisit(hunter1, forest1, LocalDate.now());
        service.createVisit(newVisit);
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2, newVisit);
    }

    @Test
    public void createVisit_nullVisit() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        assertThatThrownBy(() -> service.createVisit(null)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void createVisit_invalidVisit_nullForest() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        Visit newVisit = createVisit(hunter1, forest1, LocalDate.now());
        newVisit.setForest(null);
        assertThatThrownBy(() -> service.createVisit(newVisit)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void createVisit_invalidVisit_nullHunter() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        Visit newVisit = createVisit(hunter1, forest1, LocalDate.now());
        newVisit.setHunter(null);
        assertThatThrownBy(() -> service.createVisit(newVisit)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void createVisit_invalidVisit_nullDate() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        Visit newVisit = createVisit(hunter1, forest1, LocalDate.now());
        newVisit.setDate(null);
        assertThatThrownBy(() -> service.createVisit(newVisit)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void createVisit_duplicateVisit() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        Visit newVisit = createVisit(visit1.getHunter(), visit1.getForest(), LocalDate.now());
        newVisit.setForest(visit1.getForest());
        newVisit.setHunter(visit1.getHunter());
        newVisit.setDate(visit1.getDate());
        assertThatThrownBy(() -> service.createVisit(newVisit)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void createVisit_invalidVisit_hasId() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        Visit newVisit = createVisit(visit1.getHunter(), visit1.getForest(), LocalDate.now());
        newVisit.setId(15L);
        assertThatThrownBy(() -> service.createVisit(newVisit)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void deleteVisit_valid() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        service.deleteVisit(visit1);
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit2);

        service.deleteVisit(visit2);
        assertThat(persistedVisits.values()).isEmpty();
    }

    @Test
    public void deleteVisit_nullVisit() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        assertThatThrownBy(() -> service.deleteVisit(null)).isInstanceOf(DataAccessException.class);
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    public void deleteVisit_invalidVisit_nullId() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        visit1.setId(null);
        assertThatThrownBy(() -> service.deleteVisit(visit1)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void deleteVisit_sameEntityTwice() {
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit1, visit2);
        service.deleteVisit(visit1);
        assertThat(persistedVisits.values()).containsExactlyInAnyOrder(visit2);

        assertThatThrownBy(() -> service.deleteVisit(visit1)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void updateVisit_valid_mainAttributes() {
        visit1.setNote("Another note");
        visit1.setDate(LocalDate.of(2015, Month.APRIL, 12));
        visit1.setForest(visit2.getForest());
        visit1.setHunter(visit2.getHunter());

        service.updateVisit(visit1);

        assertThat(persistedVisits.values()).contains(visit1);
        Visit persistedVisit = persistedVisits.get(visit1.getId());
        assertThat(persistedVisit.getNote()).isEqualTo("Another note");
        assertThat(persistedVisit.getDate()).isEqualTo(LocalDate.of(2015, Month.APRIL, 12));
        assertThat(persistedVisit.getForest()).isEqualTo(visit2.getForest());
        assertThat(persistedVisit.getHunter()).isEqualTo(visit2.getHunter());
    }

    @Test
    public void updateVisit_valid_addMushroom() {
        Mushroom newMushroom = createMushroom("New", MushroomType.INEDIBLE, "October", "October");
        visit1.addMushroom(newMushroom);
        service.updateVisit(visit1);

        assertThat(persistedVisits.get(visit1.getId()).getMushrooms()).containsExactlyInAnyOrder(mushroom1, newMushroom);
    }


    @Test
    public void updateVisit_invalid_nullForest() {
        visit1.setForest(null);
        assertThatThrownBy(() -> service.updateVisit(visit1)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void updateVisit_invalid_nullHunter() {
        visit1.setHunter(null);
        assertThatThrownBy(() -> service.updateVisit(visit1)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void updateVisit_invalid_nullDate() {
        visit1.setDate(null);
        assertThatThrownBy(() -> service.updateVisit(visit1)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void updateVisit_invalid_duplicateVisit() {
        visit1.setHunter(visit2.getHunter());
        visit1.setForest(visit2.getForest());
        visit1.setDate(visit2.getDate());
        assertThatThrownBy(() -> service.updateVisit(visit1)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void updateVisit_invalid_nullVisit() {
        assertThatThrownBy(() -> service.updateVisit(null)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void getVisitsByHunter() {
        assertThat(service.getVisitsByHunter(hunter1)).containsExactly(visit1);
        assertThat(service.getVisitsByHunter(hunter2)).containsExactly(visit2);
    }

    // dependent on create
    @Test
    public void getVisitsByHunter_more() {
        Visit newVisit = createVisit(hunter1, forest1, LocalDate.now());
        service.createVisit(newVisit);

        assertThat(service.getVisitsByHunter(hunter1)).containsExactlyInAnyOrder(visit1, newVisit);
        assertThat(service.getVisitsByHunter(hunter2)).containsExactly(visit2);
    }

    @Test
    public void getVisitsByHunter_nullHunter() {
        assertThatThrownBy(() -> service.getVisitsByHunter(null)).isInstanceOf(DataAccessException.class);
    }

    @Test
    public void getVisitsByForest() {
        assertThat(service.getVisitsByForest(forest1)).containsExactly(visit2);
        assertThat(service.getVisitsByForest(forest2)).containsExactly(visit1);
    }

    // dependent on create
    @Test
    public void getVisitsByForest_more() {
        Visit newVisit = createVisit(hunter1, forest1, LocalDate.now());
        service.createVisit(newVisit);

        assertThat(service.getVisitsByForest(forest1)).containsExactlyInAnyOrder(visit2, newVisit);
        assertThat(service.getVisitsByForest(forest2)).containsExactly(visit1);
    }

    @Test
    public void getVisitsByForest_nullForest() {
        assertThatThrownBy(() -> service.getVisitsByForest(null)).isInstanceOf(DataAccessException.class);
    }


    @Test
    public void getVisitsByMushroom() {
        assertThat(service.getVisitsByMushroom(mushroom1)).containsExactlyInAnyOrder(visit1, visit2);
        assertThat(service.getVisitsByMushroom(mushroom2)).containsExactly(visit2);
    }

    // dependent on create
    @Test
    public void getVisitsByMushroom_more() {
        Visit newVisit = createVisit(hunter1, forest1, LocalDate.now());
        newVisit.addMushroom(mushroom2);
        service.createVisit(newVisit);

        assertThat(service.getVisitsByMushroom(mushroom1)).containsExactlyInAnyOrder(visit1, visit2);
        assertThat(service.getVisitsByMushroom(mushroom2)).containsExactly(visit2, newVisit);
    }

    @Test
    public void getVisitsMushroom_nullMushroom() {
        assertThatThrownBy(() -> service.getVisitsByMushroom(null)).isInstanceOf(DataAccessException.class);
    }

}