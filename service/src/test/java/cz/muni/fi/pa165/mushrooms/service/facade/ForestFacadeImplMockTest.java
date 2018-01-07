package cz.muni.fi.pa165.mushrooms.service.facade;

import cz.muni.fi.pa165.mushrooms.dto.ForestDTO;
import cz.muni.fi.pa165.mushrooms.entity.Forest;
import cz.muni.fi.pa165.mushrooms.entity.Mushroom;
import cz.muni.fi.pa165.mushrooms.enums.MushroomType;
import cz.muni.fi.pa165.mushrooms.service.BeanMappingService;
import cz.muni.fi.pa165.mushrooms.service.ForestService;
import cz.muni.fi.pa165.mushrooms.service.MushroomService;
import cz.muni.fi.pa165.mushrooms.service.config.ServiceConfiguration;
import mockit.Deencapsulation;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author bencikpeter
 */

@ContextConfiguration(classes = ServiceConfiguration.class)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class ForestFacadeImplMockTest extends AbstractTransactionalJUnit4SpringContextTests {

    private Forest forest1, forest2;
    private ForestDTO forestDTO1, forestDTO2;

    @Injectable
    private ForestService service;
    @Injectable
    private MushroomService mushroomService;
    @Inject
    @Tested // both annotations are necessary
    private Mapper dozer;
    @Inject
    @Tested
    private BeanMappingService mapping;
    @Tested(fullyInitialized = true)
    private ForestFacadeImpl facade;

    @Before
    public void setUp() {
        forest1 = new Forest();
        forest1.setName("Nice forest");
        forest1.setDescription("Nice forest in Slovakia");
        Deencapsulation.setField(forest1, "id", 1L);

        forest2 = new Forest();
        forest2.setName("Ugly forest");
        forest2.setDescription("Ugly forest in ugly country");
        Deencapsulation.setField(forest1, "id", 2L);

        forestDTO1 = mapping.mapTo(forest1, ForestDTO.class);
        forestDTO2 = mapping.mapTo(forest2, ForestDTO.class);

        new Expectations() {{
            service.findForestById(anyLong);
            result = new Delegate() {
                Forest foo(Long id) {
                    if (id.equals(1L)) return forest1;
                    if (id.equals(2L)) return forest2;
                    return null;
                }
            };
            minTimes = 0;

            service.findForestByName(anyString);
            result = new Delegate() {
                Forest foo(String name) {
                    if (name.equals("Nice forest")) return forest1;
                    if (name.equals("Ugly forest")) return forest2;
                    return null;
                }
            };
            minTimes = 0;

            service.findAllForests();
            result = new Delegate() {
                List<Forest> foo() {
                    List<Forest> list = new ArrayList<>();
                    list.add(forest1);
                    list.add(forest2);
                    return list;
                }
            };
            minTimes = 0;

            service.deleteForest((Forest) any);
            result = new Delegate() {
                void foo(Forest forest) {
                    //no action performed
                }
            };
            minTimes = 0;

            service.updateForest((Forest) any);
            result = new Delegate() {
                void foo(Forest forest) {
                    //no action performed
                }
            };
            minTimes = 0;

            service.createForest((Forest) any);
            result = new Delegate() {
                void foo(Forest forest) {
                    //no action performed
                }
            };
            minTimes = 0;

            service.findAllForestsWithMushroom((Mushroom) any);
            result = new Delegate() {
                List<Forest> foo(Mushroom mushroom) {
                    //always returns forest1, forest2,
                    //correct list is not a point of this test
                    //no functionality yet

                    return null;
                }
            };
            minTimes = 0;

            mushroomService.findMushroomById(anyLong);
            result = new Delegate() {
                Mushroom foo(Long id) {
                    Mushroom m = new Mushroom();
                    m.setType(MushroomType.INEDIBLE);
                    m.setName("Dont eat me");
                    m.setId(id);

                    return m;
                }
            };
            minTimes = 0;

        }};
    }

    @Test
    public void findByNameTest() {
        assertThat(facade.findByName(forest1.getName())).isEqualToComparingFieldByField(forestDTO1);
        assertThat(facade.findByName(forest2.getName())).isEqualToComparingFieldByField(forestDTO2);
        assertThat(facade.findByName("lalala")).isNull();
    }

    @Test
    public void findByIdTest() {
        assertThat(facade.findById(1L)).isEqualToComparingFieldByField(forestDTO1);
        assertThat(facade.findById(2L)).isEqualToComparingFieldByField(forestDTO2);
        assertThat(facade.findById(123L)).isNull();
    }

    @Test
    public void findAllForestsTest() {
        assertThat(facade.findAllForests()).containsExactlyInAnyOrder(forestDTO1, forestDTO2);
    }

    //Delete, update, create not testable very well by mock - no access to database,
    //testing only error handling
    @Test
    public void deleteForestTest() {
        assertThatThrownBy(() -> facade.deleteForest(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void updateForestTest() {
        assertThatThrownBy(() -> facade.updateForest(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createForest() {
        assertThatThrownBy(() -> facade.createForest(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void listAllForestsWithMushroomTest() {
        //TODO
    }
}
