package cz.muni.fi.pa165.mushrooms.dao;

import cz.muni.fi.pa165.mushrooms.entity.Mushroom;
import cz.muni.fi.pa165.mushrooms.enums.MushroomType;
import cz.muni.fi.pa165.mushrooms.validation.PersistenceSampleApplicationContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * @author bencikpeter
 */
@ContextConfiguration(classes = PersistenceSampleApplicationContext.class)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class MushroomDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private MushroomDao mushroomDao;

    @PersistenceContext
    private EntityManager em;


    private Mushroom mushroom1;
    private Mushroom mushroom2;

    @Before
    public void setUp() {
        // create mushrooms
        mushroom1 = createMushroom("toadstool", MushroomType.POISONOUS, "June", "July");
        mushroom2 = createMushroom("champignon", MushroomType.EDIBLE, "May", "September");

        // persist mushrooms

        em.persist(mushroom1);
        em.persist(mushroom2);

        em.flush();
    }

    private static Mushroom createMushroom(String name, MushroomType type, String beginMonth, String endMonth) {
        Mushroom mushroom = new Mushroom();
        mushroom.setName(name);
        mushroom.setType(type);
        mushroom.setIntervalOfOccurrence(beginMonth, endMonth);

        return mushroom;
    }

    @Test
    public void findById_validId() throws Exception {
        Mushroom mushroom = mushroomDao.findById(mushroom1.getId());
        assertThat(mushroom).isNotNull();
        assertThat(mushroom).isEqualTo(mushroom1);
    }

    @Test
    public void findById_invalidId() throws Exception {
        Mushroom mushroom = mushroomDao.findById(mushroom1.getId() + mushroom2.getId());
        assertThat(mushroom).isNull();
    }

    @Test
    public void findById_nullId() throws Exception {
        assertThatThrownBy(() -> mushroomDao.findById(null)).hasRootCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void create() throws Exception {
        Mushroom mushroom = createMushroom("WeirdMushroom", MushroomType.INEDIBLE, "May", "July");
        mushroomDao.create(mushroom);

        List<Mushroom> list = em.createQuery("select m from Mushroom m", Mushroom.class)
                .getResultList();

        assertThat(list).containsExactlyInAnyOrder(mushroom1, mushroom2, mushroom);

    }

    @Test
    public void create_nullName() throws Exception {
        Mushroom mushroom = createMushroom(null, MushroomType.INEDIBLE, "May", "July");

        assertThatThrownBy(() -> mushroomDao.create(mushroom)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void create_nullMushroomType() throws Exception {
        Mushroom mushroom = createMushroom("WierdMushroom", null, "May", "July");

        assertThatThrownBy(() -> mushroomDao.create(mushroom)).isInstanceOf(ConstraintViolationException.class);
    }

    // TODO this string validation is not implemented yet - tests prepared for the future
//    @Test
//    public void create_nullBeginMonth() throws Exception {
//        Mushroom mushroom = createMushroom("WierdMushroom", MushroomType.INEDIBLE, null, "July");
//
//        assertThatThrownBy(() -> mushroomDao.create(mushroom)).isInstanceOf(ConstraintViolationException.class);
//    }
//
//    @Test
//    public void create_nullLastMonth() throws Exception {
//        Mushroom mushroom = createMushroom("WierdMushroom", MushroomType.INEDIBLE, "June", null);
//
//        assertThatThrownBy(() -> mushroomDao.create(mushroom)).isInstanceOf(ConstraintViolationException.class);
//    }

    @Test
    public void create_nullEntity() throws Exception {

        assertThatThrownBy(() -> mushroomDao.create(null)).hasRootCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void delete() throws Exception {

        mushroomDao.delete(mushroom1);
        em.flush();

        List<Mushroom> list = em.createQuery("select m from Mushroom m", Mushroom.class).getResultList();

        assertThat(list).hasSize(1);

    }

    @Test
    public void delete_entityNotPersisted() throws Exception {
        Mushroom mush = createMushroom("mush", MushroomType.INEDIBLE, "May", "June");
        mushroomDao.delete(mush); // should not do anything
        em.flush();
        assertThat(mushroomDao.findAll()).containsExactlyInAnyOrder(mushroom1, mushroom2);
    }

    @Test
    public void delete_nullEntity() throws Exception {
        assertThatThrownBy(() -> mushroomDao.delete(null)).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findAll() throws Exception {

        List<Mushroom> list = mushroomDao.findAll();

        assertThat(list).containsExactlyInAnyOrder(mushroom1, mushroom2);
    }

    @Test
    public void findByName_validName() throws Exception {

        Mushroom mushroom = mushroomDao.findByName(mushroom1.getName());
        assertThat(mushroom).isNotNull();
        assertThat(mushroom).isEqualTo(mushroom1);
    }

    @Test
    public void findByName_invalidName() throws Exception {

        Mushroom mushroom = mushroomDao.findByName("randomName");
        assertThat(mushroom).isNull();
    }


    @Test
    public void findByName_nullName() throws Exception {

        assertThatThrownBy(() -> mushroomDao.findByName(null))
                .hasRootCauseExactlyInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void findByMushroomType() throws Exception {
        List<Mushroom> list = mushroomDao.findByMushroomType(MushroomType.EDIBLE);
        assertThat(list).containsExactlyInAnyOrder(mushroom2);
    }

    @Test
    public void findByMushroomType_empty() throws Exception {
        List<Mushroom> list = mushroomDao.findByMushroomType(MushroomType.INEDIBLE);
        assertThat(list).isEmpty();
    }

    @Test
    public void update() throws Exception {
        mushroom1.setType(MushroomType.EDIBLE);
        mushroomDao.update(mushroom1);
        em.flush();

        Mushroom mushroom = em.createQuery("select m from Mushroom m where m.name = :name", Mushroom.class).setParameter("name", mushroom1.getName()).getSingleResult();

        assertThat(mushroom).isEqualTo(mushroom1);
    }

    @Test
    public void update_validManaged() throws Exception {
        Mushroom mushroom = mushroomDao.findById(mushroom1.getId());
        mushroom.setType(MushroomType.EDIBLE);
        mushroomDao.update(mushroom);
        em.flush();

        Mushroom mush = em.createQuery("select m from Mushroom m where m.name = :name", Mushroom.class).setParameter("name", mushroom1.getName()).getSingleResult();

        assertThat(mush).isEqualTo(mushroom);
    }

    @Test
    public void update_nullName() throws Exception {
        mushroom1.setName(null);
        mushroomDao.update(mushroom1);
        assertThatThrownBy(() -> em.flush()).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void update_duplicateName() throws Exception {
        mushroom1.setName(mushroom2.getName());
        mushroomDao.update(mushroom1);
        assertThatThrownBy(() -> em.flush()).isInstanceOf(PersistenceException.class);
    }

    @Test
    public void update_nullType() throws Exception {
        mushroom1.setType(null);
        mushroomDao.update(mushroom1);
        assertThatThrownBy(() -> em.flush()).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void update_nullEntity() throws Exception {
        assertThatThrownBy(() -> mushroomDao.update(null)).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void findByIntervaOfOccurence() throws Exception {   //// TODO Date - String
//
//        List<Mushroom> list = mushroomDao.findByIntervalOfOccurrence("June", "July");
//
//        assertThat(list).containsExactlyInAnyOrder(mushroom1);
    }

}
