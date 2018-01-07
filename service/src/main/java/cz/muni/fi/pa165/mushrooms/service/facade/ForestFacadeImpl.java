package cz.muni.fi.pa165.mushrooms.service.facade;

import cz.muni.fi.pa165.mushrooms.dto.AddEditForestDTO;
import cz.muni.fi.pa165.mushrooms.dto.ForestDTO;
import cz.muni.fi.pa165.mushrooms.dto.MushroomDTO;
import cz.muni.fi.pa165.mushrooms.entity.Forest;
import cz.muni.fi.pa165.mushrooms.entity.Mushroom;
import cz.muni.fi.pa165.mushrooms.facade.ForestFacade;
import cz.muni.fi.pa165.mushrooms.service.BeanMappingService;
import cz.muni.fi.pa165.mushrooms.service.ForestService;
import cz.muni.fi.pa165.mushrooms.service.MushroomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The facade/wrapper "maps" to the APIs of the subsystem of the Forest.
 *
 * @author bencikpeter
 */
@Transactional
@Service
public class ForestFacadeImpl implements ForestFacade {

    @Inject
    private ForestService service;

    @Inject
    private MushroomService mushroomService;

    @Inject
    private BeanMappingService beanMappingService;


    @Override
    public ForestDTO findByName(String name) {
        Forest forest = service.findForestByName(name);

        if (forest == null) return null;
        ForestDTO forestDTO = beanMappingService.mapTo(forest, ForestDTO.class);
        return forestDTO;
    }

    @Override
    public ForestDTO findById(Long id) {
        Forest forest = service.findForestById(id);

        if (forest == null) return null;
        ForestDTO forestDTO = beanMappingService.mapTo(forest, ForestDTO.class);
        return forestDTO;
    }

    @Override
    public void deleteForest(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Null id at forest delete.");
        }
        Forest forest = service.findForestById(id);
        service.deleteForest(forest);
        return;
    }

    @Override
    public List<ForestDTO> findAllForests() {
        return beanMappingService.mapTo(service.findAllForests(), ForestDTO.class);
    }

    @Override
    public ForestDTO updateForest(AddEditForestDTO forest) {
        if (forest == null) {
            throw new IllegalArgumentException("Null forestDTO cannot be updated");
        }
        Forest entityForest = service.findForestById(forest.getId());
        if (entityForest == null) {
            throw new IllegalArgumentException("no forest found for id " + forest.getId());
        }
        entityForest.setDescription(forest.getDescription());
        entityForest.setName(forest.getName());
        service.updateForest(entityForest);

        return beanMappingService.mapTo(service.findForestById(forest.getId()), ForestDTO.class);
    }

    @Override
    public ForestDTO createForest(AddEditForestDTO forest) {
        if (forest == null) {
            throw new IllegalArgumentException("Null forestDTO cannot be updated");
        }
        Forest newForest = new Forest();
        newForest.setName(forest.getName());
        newForest.setDescription(forest.getDescription());

        service.createForest(newForest);

        return beanMappingService.mapTo(service.findForestByName(forest.getName()), ForestDTO.class);
    }

    @Override
    public List<ForestDTO> listAllForestsWithMushroom(MushroomDTO mushroom) {
        if (mushroom == null) throw new IllegalArgumentException();
        Mushroom mushroomEntity = mushroomService.findMushroomById(mushroom.getId());

        List<Map.Entry<Forest, Integer>> forestsPairs = service.findAllForestsWithMushroom(mushroomEntity);
        List<Forest> forestsEntities = new ArrayList<>();

        for (Map.Entry<Forest, Integer> entry : forestsPairs) {
            forestsEntities.add(entry.getKey());
        }

        return beanMappingService.mapTo(forestsEntities, ForestDTO.class);
    }
}
