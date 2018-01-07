package cz.muni.fi.pa165.mushrooms.service.facade;

import cz.muni.fi.pa165.mushrooms.service.TestUtils;
import cz.muni.fi.pa165.mushrooms.dto.MushroomDTO;
import cz.muni.fi.pa165.mushrooms.entity.Mushroom;
import cz.muni.fi.pa165.mushrooms.enums.MushroomType;
import cz.muni.fi.pa165.mushrooms.facade.MushroomFacade;
import cz.muni.fi.pa165.mushrooms.service.BeanMappingService;
import cz.muni.fi.pa165.mushrooms.service.MushroomService;
import cz.muni.fi.pa165.mushrooms.service.config.ServiceConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for facade functionality without mock - using the real service implementation.
 *
 * @author Lindar84
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class MushroomFacadeImplNoMockTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    MushroomService service;
    @Inject
    MushroomFacade facade;
    @Inject
    private BeanMappingService mapping;

    private Mushroom mushroom1;
    private Mushroom mushroom2;

    private MushroomDTO mushroom1DTO;
    private MushroomDTO mushroom2DTO;

    @Before
    public void setUp() {
        mushroom1 = TestUtils.createMushroom("toadstool", MushroomType.POISONOUS, "April", "September");
        service.createMushroom(mushroom1);
        assertNotNull(mushroom1.getId());

        mushroom2 = TestUtils.createMushroom("puffball", MushroomType.EDIBLE, "June", "August");
        service.createMushroom(mushroom2);
        assertNotNull(mushroom2.getId());

        mushroom1DTO = mapping.mapTo(mushroom1, MushroomDTO.class);
        assertNotNull(mushroom1DTO);
        mushroom2DTO = mapping.mapTo(mushroom2, MushroomDTO.class);
        assertNotNull(mushroom2DTO);
    }

    @Test
    public void findAllMushrooms() {
        assertThat(facade.findAllMushrooms()).containsExactlyInAnyOrder(mushroom1DTO, mushroom2DTO);
    }

    @Test
    public void findMushroomById() {
        assertThat(facade.findMushroomById(mushroom1.getId())).isEqualToComparingFieldByField(mushroom1DTO);
        assertThat(facade.findMushroomById(mushroom2.getId())).isEqualToComparingFieldByField(mushroom2DTO);
        assertThat(facade.findMushroomById(999L)).isNull();
    }

    @Test
    public void findMushroomByName() {
        assertThat(facade.findMushroomByName("toadstool")).isEqualToComparingFieldByField(mushroom1DTO);
        assertThat(facade.findMushroomByName("puffball")).isEqualToComparingFieldByField(mushroom2DTO);
        assertThat(facade.findMushroomByName("foo")).isNull();
    }

    @Test
    public void findByMushroomType() {
        assertThat(facade.findByMushroomType(MushroomType.POISONOUS)).containsExactly(mushroom1DTO);
        assertThat(facade.findByMushroomType(MushroomType.EDIBLE)).containsExactly(mushroom2DTO);
        assertThat(facade.findByMushroomType(MushroomType.INEDIBLE)).isEmpty();
    }

    @Test
    public void findByIntervalOfOccurrence() {       /////// TODO String - Date
//        assertThat(facade.findByIntervalOfOccurrence("April", "April")).containsExactly(mushroom1DTO);
//        assertThat(facade.findByIntervalOfOccurrence("August", "August"))
//                .containsExactlyInAnyOrder(mushroom1DTO, mushroom2DTO);
//        assertThat(facade.findByIntervalOfOccurrence("January", "March")).isEmpty();
    }

    @Test
    public void createMushroom() {
        MushroomDTO newMushroomDTO = new MushroomDTO();
        newMushroomDTO.setName("bolete");
        newMushroomDTO.setType(MushroomType.EDIBLE);
        newMushroomDTO.setIntervalOfOccurrence("March - August");

        MushroomDTO equalMushroomDTO = new MushroomDTO();
        equalMushroomDTO.setName("bolete");
        equalMushroomDTO.setType(MushroomType.EDIBLE);
        equalMushroomDTO.setIntervalOfOccurrence("March - August");

        assertThat(facade.createMushroom(newMushroomDTO)).isEqualTo(equalMushroomDTO);
    }

    @Test
    public void deleteMushroom() {
        assertThat(facade.deleteMushroom(mushroom1.getId())).isTrue();
        assertThatThrownBy(() -> facade.deleteMushroom(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateMushroom() {
        MushroomDTO update = new MushroomDTO();
        update.setId(mushroom1.getId());
        update.setName("toad");
        update.setType(MushroomType.INEDIBLE);
        update.setIntervalOfOccurrence("May - October");
        MushroomDTO updated = facade.updateMushroom(update);

        assertThat(updated.getId()).isEqualTo(mushroom1.getId());
        assertThat(updated.getName()).isEqualTo("toad");
        assertThat(updated.getType()).isEqualTo(MushroomType.INEDIBLE);
        assertThat(updated.getIntervalOfOccurrence()).isEqualTo("May - October");
    }

}
